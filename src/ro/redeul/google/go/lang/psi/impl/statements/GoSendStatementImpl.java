package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoSendStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoSendStatementImpl extends GoPsiElementBase implements GoSendStatement {

    public GoSendStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public GoExpr getChannelExpr() {
        // TODO mtoader this shouldn't be null here
        return null;
    }

    @NotNull
    @Override
    public GoExpr getValueExpr() {
        // TODO mtoader this shouldn't be null here
        return null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSendStatement(this);
    }
}
