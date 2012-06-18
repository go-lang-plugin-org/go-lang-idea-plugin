package ro.redeul.google.go.lang.psi.visitors;

import java.util.Map;

import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeName;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/15/11
 * Time: 8:20 AM
 */
public class GoImportUsageCheckingVisitor extends GoRecursiveElementVisitor {

    private Map<String, GoImportDeclaration> imports;

    public GoImportUsageCheckingVisitor(Map<String, GoImportDeclaration> imports)
    {
        this.imports = imports;
    }

    @Override
    public void visitLiteralExpression(GoLiteralExpression expression) {
        GoLiteral literal = expression.getLiteral();

        if ( literal != null && literal.getType() == GoLiteral.Type.Identifier ) {
            checkQualifiedIdentifier((GoLiteralIdentifier) literal);
        }
    }

    private void checkQualifiedIdentifier(GoLiteralIdentifier identifier) {
        if ( identifier != null && identifier.isQualified() ) {
            imports.remove(identifier.getLocalPackageName());
        }
    }

    @Override
    public void visitTypeName(GoTypeName typeName) {
        checkQualifiedIdentifier(typeName.getIdentifier());
    }
}
