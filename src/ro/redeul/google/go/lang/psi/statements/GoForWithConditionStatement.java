package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoForWithConditionStatement extends GoForStatement {

    GoExpr getCondition();

}
