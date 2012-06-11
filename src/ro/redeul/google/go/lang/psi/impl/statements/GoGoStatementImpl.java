package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoGoStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoGoStatementImpl extends GoPsiElementBase implements GoGoStatement {
    public GoGoStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitGoStatement(this);
    }
}
