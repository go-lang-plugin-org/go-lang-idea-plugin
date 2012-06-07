package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoForWithClausesStatement extends GoForStatement {

    GoStatement getInitialStatement();

    GoExpr getCondition();

    GoStatement getPostStatement();
}
