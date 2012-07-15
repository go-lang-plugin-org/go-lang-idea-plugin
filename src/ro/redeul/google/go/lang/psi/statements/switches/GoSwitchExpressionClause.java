package ro.redeul.google.go.lang.psi.statements.switches;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public interface GoSwitchExpressionClause extends GoPsiElement {

    @NotNull
    GoExpr[] getExpressions();

    @NotNull
    GoStatement[] getStatements();

    boolean isDefault();
}
