package ro.redeul.google.go.lang.psi.impl.statements.switches;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeStatement;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoSwitchTypeStatementImpl extends GoPsiElementBase
    implements GoSwitchTypeStatement {

    public GoSwitchTypeStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoSimpleStatement getSimpleStatement() {
        return findChildByClass(GoSimpleStatement.class);
    }

    @Override
    public GoSwitchTypeGuard getTypeSwitchGuard() {
        return findChildByClass(GoSwitchTypeGuard.class);
    }

    @NotNull
    @Override
    public GoSwitchTypeClause[] getCaseClauses() {
        return findChildrenByClass(GoSwitchTypeClause.class);
    }
}
