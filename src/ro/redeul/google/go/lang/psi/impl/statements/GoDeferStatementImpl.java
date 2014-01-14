package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoDeferStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoDeferStatementImpl extends GoPsiElementBase implements GoDeferStatement {
    public GoDeferStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitDeferStatement(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementDefer(this, data);
    }
}
