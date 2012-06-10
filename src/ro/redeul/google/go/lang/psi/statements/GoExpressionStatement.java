package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoExpressionStatement extends GoSimpleStatement {

    GoExpr getExpression();

}
