package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoForWithConditionStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoForWithConditionStatementImpl extends GoForStatementImpl
    implements GoForWithConditionStatement {
    public GoForWithConditionStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getCondition() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitForWithCondition(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementForWithCondition(this, data);
    }
}
