package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoForWithRangeStatement extends GoForStatement {

    GoExpr getKey();

    GoExpr getValue();

    boolean isDeclaration();

    GoExpr getRangeExpression();
}
