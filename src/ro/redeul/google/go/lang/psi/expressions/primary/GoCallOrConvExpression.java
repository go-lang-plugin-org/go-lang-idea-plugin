package ro.redeul.google.go.lang.psi.expressions.primary;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoCallOrConvExpression extends GoPrimaryExpression {

    GoPrimaryExpression getBaseExpression();

    GoPsiType getTypeArgument();

    GoExpr[] getArguments();

    boolean isVariadic();
}
