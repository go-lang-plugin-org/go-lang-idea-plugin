package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoGotoStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoGotoStatementImpl extends GoPsiElementBase
        implements GoGotoStatement {

    public GoGotoStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralIdentifier getLabel() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitGotoStatement(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementGoto(this, data);
    }
}
