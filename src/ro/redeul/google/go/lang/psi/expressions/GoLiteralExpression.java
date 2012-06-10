package ro.redeul.google.go.lang.psi.expressions;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;

public interface GoLiteralExpression extends GoExpr {

   GoLiteral getLiteral();

}
