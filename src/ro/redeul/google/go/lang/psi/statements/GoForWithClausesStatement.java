package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoForWithClausesStatement extends GoForStatement {

    GoSimpleStatement getInitialStatement();

    GoExpr getCondition();

    GoSimpleStatement getPostStatement();
}
