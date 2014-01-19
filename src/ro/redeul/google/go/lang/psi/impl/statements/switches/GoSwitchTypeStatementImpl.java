package ro.redeul.google.go.lang.psi.impl.statements.switches;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoDocumentedPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeStatement;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoSwitchTypeStatementImpl extends GoDocumentedPsiElementBase implements GoSwitchTypeStatement {

    public GoSwitchTypeStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoSimpleStatement getSimpleStatement() {
        return findChildByClass(GoSimpleStatement.class);
    }

    @NotNull
    @Override
    public GoSwitchTypeGuard getTypeGuard() {
        //noinspection ConstantConditions
        return findChildByClass(GoSwitchTypeGuard.class);
    }

    @NotNull
    @Override
    public GoSwitchTypeClause[] getClauses() {
        return findChildrenByClass(GoSwitchTypeClause.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if (lastParent == null )
            return true;

        GoSwitchTypeGuard typeGuard = getTypeGuard();
        if ( lastParent != typeGuard ) {
            if (!typeGuard.processDeclarations(processor, state, null, place))
                return false;
        }

        GoSimpleStatement initStatement = getSimpleStatement();
        return !(initStatement != null && lastParent != initStatement) || initStatement.processDeclarations(processor, state, null, place);

    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementSwitchType(this, data);
    }
}
