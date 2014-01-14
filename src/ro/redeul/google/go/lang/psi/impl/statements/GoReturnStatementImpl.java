package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoReturnStatementImpl extends GoPsiElementBase
    implements GoReturnStatement {
    public GoReturnStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr[] getExpressions() {
        GoExpressionList expressionList = findChildByClass(GoExpressionList.class);
        if  (expressionList != null)
            return expressionList.getExpressions();

        return findChildrenByClass(GoExpr.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitReturnStatement(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementReturn(this, data);
    }
}
