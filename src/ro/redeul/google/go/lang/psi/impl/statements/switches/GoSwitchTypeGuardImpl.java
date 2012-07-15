package ro.redeul.google.go.lang.psi.impl.statements.switches;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoSwitchTypeGuardImpl extends GoPsiElementBase
    implements GoSwitchTypeGuard {

    public GoSwitchTypeGuardImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralIdentifier getIdentifier() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }
}
