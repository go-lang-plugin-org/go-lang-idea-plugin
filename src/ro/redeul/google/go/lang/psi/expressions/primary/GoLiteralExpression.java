package ro.redeul.google.go.lang.psi.expressions.primary;

import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;

public interface GoLiteralExpression extends GoPrimaryExpression {

   GoLiteral getLiteral();

}
