package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIndexExpression;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoIndexExpressionImpl extends GoExpressionBase
    implements GoIndexExpression
{
    public GoIndexExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType resolveType() {
        // TODO: fix this
        return null;
    }

    @Override
    public GoExpr getIndex() {
        return findChildByClass(GoExpr.class, 0);
    }

    @Override
    public GoExpr getIndexed() {
        return findChildByClass(GoExpr.class, 1);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitIndexExpression(this);
    }
}
