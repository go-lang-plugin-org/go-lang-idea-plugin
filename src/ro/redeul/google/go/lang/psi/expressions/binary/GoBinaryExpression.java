package ro.redeul.google.go.lang.psi.expressions.binary;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.Operator;

public interface GoBinaryExpression<Op extends Operator> extends GoExpr {

    /**
     * Use the {@link #Op()} method
     */
    @Deprecated
    IElementType getOperator();

    @NotNull
    Op Op();

    GoExpr getLeftOperand();

    GoExpr getRightOperand();

}
