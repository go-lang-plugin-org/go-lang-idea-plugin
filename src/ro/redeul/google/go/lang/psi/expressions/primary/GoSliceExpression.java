package ro.redeul.google.go.lang.psi.expressions.primary;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;

public interface GoSliceExpression extends GoPrimaryExpression {

    GoPrimaryExpression getBaseExpression();

    @Nullable
    GoExpr getFirstIndex();

    @Nullable
    GoExpr getSecondIndex();

    @Nullable
    GoExpr getCapacity();
}
