package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;

public abstract class GoPrimaryExpressionBase extends GoExpressionBase implements GoPrimaryExpression {

    protected GoPrimaryExpressionBase(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getExpression() {
        return this;
    }

    @NotNull
    @Override
    public Op getOp() {
        return Op.None;
    }
}
