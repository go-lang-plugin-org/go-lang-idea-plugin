package ro.redeul.google.go.lang.psi.visitors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.resolve.refs.PackageReference;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/15/11
 * Time: 8:20 AM
 */
public class GoImportUsageCheckingVisitor extends GoElementVisitorWithData<Set<GoImportDeclaration>> {

    public GoImportUsageCheckingVisitor(Set<GoImportDeclaration> imports) {
        setData(imports);
    }

    @Override
    public void visitElement(GoPsiElement element) {
        if (element != null) {
            element.acceptChildren(this);
        }
    }

    @Override
    public void visitLiteralExpression(GoLiteralExpression expression) {
        GoLiteral literal = expression.getLiteral();

        if (literal == null)
            return;

        switch (literal.getType()) {
            case Identifier:
                checkIdentifierReference((GoLiteralIdentifier) literal);
                break;
            case Composite:
            case Function:
                visitElement(expression);
        }
    }

    @Override
    public void visitTypeName(GoPsiTypeName typeName) {
        checkIdentifierReference(typeName.getQualifier());
    }

    private void checkIdentifierReference(GoLiteralIdentifier identifier) {
        if (identifier != null) {

            PsiReference refs[] = identifier.getReferences();
            for (PsiReference ref : refs) {
                if (ref instanceof PackageReference) {
                    PackageReference packageRef = (PackageReference) ref;
                    PsiElement target = packageRef.resolve();
                    if (target != null && (target instanceof GoImportDeclaration))
                        getData().remove(target);
                }
            }
        }
    }
}
