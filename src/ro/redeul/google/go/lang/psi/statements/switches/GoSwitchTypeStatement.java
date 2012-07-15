package ro.redeul.google.go.lang.psi.statements.switches;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

public interface GoSwitchTypeStatement extends GoStatement {

    @Nullable
    GoSimpleStatement getSimpleStatement();

    @Nullable
    GoSwitchTypeGuard getTypeSwitchGuard();

    @NotNull
    GoSwitchTypeClause[] getCaseClauses();
}
