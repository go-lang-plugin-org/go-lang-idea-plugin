package ro.redeul.google.go.lang.psi.impl.statements.switches;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionStatement;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoSwitchExpressionStatementImpl extends GoPsiElementBase
    implements GoSwitchExpressionStatement {

    public GoSwitchExpressionStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoSimpleStatement getSimpleStatement() {
        return findChildByClass(GoSimpleStatement.class);
    }

    @Override
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }

    @NotNull
    @Override
    public GoSwitchExpressionClause[] getClauses() {
        return findChildrenByClass(GoSwitchExpressionClause.class);
    }
}
