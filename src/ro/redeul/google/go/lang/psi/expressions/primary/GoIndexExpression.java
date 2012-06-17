package ro.redeul.google.go.lang.psi.expressions.primary;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;

public interface GoIndexExpression extends GoPrimaryExpression {

    GoPrimaryExpression getBaseExpression();

    GoExpr getIndex();
}
