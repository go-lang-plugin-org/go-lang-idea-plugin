package ro.redeul.google.go.lang.psi.expressions.binary;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.Operator;

public interface GoBinaryExpression<Op extends Operator> extends GoExpr {

    /**
     * Use the {@link #getOp()} method
     */
    @Deprecated
    IElementType getOperator();

    Op getOp();

    GoExpr getLeftOperand();

    GoExpr getRightOperand();

}
