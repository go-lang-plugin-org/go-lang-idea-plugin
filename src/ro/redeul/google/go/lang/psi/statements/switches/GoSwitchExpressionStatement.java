package ro.redeul.google.go.lang.psi.statements.switches;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

public interface GoSwitchExpressionStatement extends GoStatement {

    @Nullable
    GoSimpleStatement getSimpleStatement();

    @Nullable
    GoExpr getExpression();

    @NotNull
    GoSwitchExpressionClause[] getClauses();
}
