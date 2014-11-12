package ro.redeul.google.go.lang.psi.expressions.binary;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoBinaryExpression<Op extends Enum<Op>> extends GoExpr {

    Op op();

    GoExpr getLeftOperand();

    GoExpr getRightOperand();

}
