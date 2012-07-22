package ro.redeul.google.go.lang.psi.expressions.primary;

import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoTypeAssertionExpression extends GoPrimaryExpression {

    GoPrimaryExpression getBaseExpression();

    GoPsiType getAssertedType();
}
