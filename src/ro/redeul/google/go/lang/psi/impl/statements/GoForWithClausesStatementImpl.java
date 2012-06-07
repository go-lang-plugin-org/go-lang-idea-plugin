package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoForWithClausesStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

public class GoForWithClausesStatementImpl
    extends GoPsiElementBase
    implements GoForWithClausesStatement
{
    public GoForWithClausesStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoStatement getInitialStatement() {
        return findChildByClass(GoStatement.class, 0);
    }

    @Override
    public GoExpr getCondition() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public GoStatement getPostStatement() {
        return findChildByClass(GoStatement.class, 1);
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
