package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoDocumentedPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoExpressionStatementImpl extends GoDocumentedPsiElementBase implements GoExpressionStatement
{
    public GoExpressionStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @Nullable
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementExpression(this, data);
    }
}
