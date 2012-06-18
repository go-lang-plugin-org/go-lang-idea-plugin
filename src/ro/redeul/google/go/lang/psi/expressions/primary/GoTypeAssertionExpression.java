package ro.redeul.google.go.lang.psi.expressions.primary;

import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.types.GoType;

public interface GoTypeAssertionExpression extends GoPrimaryExpression {

    GoPrimaryExpression getBaseExpression();

    GoType getAssertedType();
}
