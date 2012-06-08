package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;

public class GoExpressionStatementImpl extends GoPsiElementBase
    implements GoExpressionStatement
{
    public GoExpressionStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @Nullable
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }
}
