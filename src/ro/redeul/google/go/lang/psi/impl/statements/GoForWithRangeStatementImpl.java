package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;

public class GoForWithRangeStatementImpl extends GoPsiElementBase
    implements GoForWithRangeStatement
{
    public GoForWithRangeStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getKey() {
        return findChildByClass(GoExpr.class, 0);
    }

    @Override
    public GoExpr getValue() {
        return findChildByClass(GoExpr.class, 1);
    }

    @Override
    public boolean isDeclaration() {
        return findChildByType(GoElementTypes.oVAR_ASSIGN) != null;
    }

    @Override
    public GoExpr getRangeExpression() {
        GoExpr[] expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length > 2) {
            return expressions[2];
        }

        if (expressions.length == 2) {
            return expressions[1];
        }

        return null;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return processor.execute(this, state);
    }
}
