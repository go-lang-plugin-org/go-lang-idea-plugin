package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoForWithClausesStatementImpl extends GoForStatementImpl implements GoForWithClausesStatement
{
    public GoForWithClausesStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoSimpleStatement getInitialStatement() {
        return findChildByClass(GoSimpleStatement.class, 0);
    }

    @Override
    public GoExpr getCondition() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public GoSimpleStatement getPostStatement() {
        return findChildByClass(GoSimpleStatement.class, 1);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitForWithClauses(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementForWithClauses(this, data);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        GoStatement init = getInitialStatement();

        if (init != null) {
            if ( ! processor.execute(init, state) )
                return false;
        }

        return processor.execute(this, state);
    }
}
