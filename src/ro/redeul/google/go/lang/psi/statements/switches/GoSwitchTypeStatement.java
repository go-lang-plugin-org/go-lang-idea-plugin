package ro.redeul.google.go.lang.psi.statements.switches;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

public interface GoSwitchTypeStatement extends GoStatement, GoDocumentedPsiElement {

    @Nullable
    GoSimpleStatement getSimpleStatement();

    @NotNull
    GoSwitchTypeGuard getTypeGuard();

    @NotNull
    GoSwitchTypeClause[] getClauses();
}
