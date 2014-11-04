package ro.redeul.google.go.lang.psi.visitors;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;

import java.util.HashSet;
import java.util.Map;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/15/11
 * Time: 8:20 AM
 */
public class GoImportUsageCheckingVisitor extends GoRecursiveElementVisitor {

    private final Map<String, GoImportDeclaration> imports;

    public GoImportUsageCheckingVisitor(Map<String, GoImportDeclaration> imports)
    {
        this.imports = imports;
    }

    @Override
    public void visitLiteralExpression(GoLiteralExpression expression) {
        GoLiteral literal = expression.getLiteral();

        if (literal == null)
            return;

        switch (literal.getType()) {
            case Identifier:
                checkQualifiedIdentifier((GoLiteralIdentifier)literal);
                break;
            case Composite:
            case Function:
                visitElement(expression);
        }
    }

    private void checkQualifiedIdentifier(GoLiteralIdentifier identifier) {
        if ( identifier != null ) {
            if ( imports.remove(identifier.getLocalPackageName()) == null ) {
                for (String s : new HashSet<String>(imports.keySet())) {
                    if (s.toLowerCase()
                         .equals(identifier.getLocalPackageName()))  {
                        imports.remove(s);
                    }
                }
            }
        }
    }

    @Override
    public void visitTypeName(GoPsiTypeName typeName) {
        checkQualifiedIdentifier(typeName.getIdentifier());
    }
}
