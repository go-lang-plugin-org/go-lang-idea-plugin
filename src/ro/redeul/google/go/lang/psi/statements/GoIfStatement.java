package ro.redeul.google.go.lang.psi.statements;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoIfStatement extends GoStatement {

    @Nullable
    GoSimpleStatement getSimpleStatement();

    GoExpr getExpression();

    GoBlockStatement getThenBlock();

    @Nullable
    GoBlockStatement getElseBlock();

    @Nullable
    GoIfStatement getElseIfStatement();
}
