package ro.redeul.google.go.lang.psi.expressions;

import org.jetbrains.annotations.NotNull;

public interface GoUnaryExpression extends GoExpr {

    enum Op {
        None, Plus, Minus, Not, Xor, Mul, Address, Pointer, Channel
    }

    @NotNull
    Op getUnaryOp();

    GoExpr getExpression();
}
