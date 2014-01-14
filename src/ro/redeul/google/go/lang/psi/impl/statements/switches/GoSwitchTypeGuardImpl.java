package ro.redeul.google.go.lang.psi.impl.statements.switches;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

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

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSwitchTypeGuard(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitSwitchTypeGuard(this, data);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return getIdentifier() == null || processor.execute(this, state);

    }
}
