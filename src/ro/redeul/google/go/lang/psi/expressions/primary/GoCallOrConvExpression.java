package ro.redeul.google.go.lang.psi.expressions.primary;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoCallOrConvExpression extends GoPrimaryExpression {

    @NotNull
    GoPrimaryExpression getBaseExpression();

    @Nullable
    GoPsiType getTypeArgument();

    @NotNull
    GoExpr[] getArguments();
}
