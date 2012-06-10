package ro.redeul.google.go.inspection;

import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public class ConstDeclarationInspection {
    public static boolean isFirstConstExpressionMissed(GoConstDeclarations constDeclarations) {
        GoConstDeclaration[] declarations = constDeclarations.getDeclarations();
        return declarations.length != 0 && declarations[0].getExpressions().length == 0;
    }

    public static boolean isMissingExpressionInConst(GoConstDeclaration constDeclaration) {
        GoLiteralIdentifier[] ids = constDeclaration.getIdentifiers();
        GoExpr[] exprs = constDeclaration.getExpressions();
        return exprs.length != 0 && ids.length > exprs.length;
    }

    public static boolean isExtraExpressionInConst(GoConstDeclaration constDeclaration) {
        GoLiteralIdentifier[] ids = constDeclaration.getIdentifiers();
        GoExpr[] exprs = constDeclaration.getExpressions();
        return exprs.length != 0 && ids.length < exprs.length;
    }
}
