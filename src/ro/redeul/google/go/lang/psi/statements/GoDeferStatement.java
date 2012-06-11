package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoDeferStatement extends GoStatement {
    GoExpr getExpression();
}
