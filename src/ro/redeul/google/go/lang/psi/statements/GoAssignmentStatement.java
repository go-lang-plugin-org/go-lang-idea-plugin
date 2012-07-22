package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;

public interface GoAssignmentStatement extends GoStatement {

    enum Op {
        Null,

        Plus, Minus, BitOr, BitXor,
        PlusEq, MinusEq, BitOrEq, BitXorEq,

        Mul, Quotient, Remainder, ShiftLeft, ShiftRight, BitAnd, BitClear,
        MulEq, QuotientEq, RemainderEq, ShiftLeftEq, ShiftRightEq, BitAndEq, BitClearEq
    }

    GoExpressionList getLeftSideExpressions();

    GoExpressionList getRightSideExpressions();

    Op getOperator();
}
