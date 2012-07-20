package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoBreakStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoBreakStatementImpl extends GoPsiElementBase
        implements GoBreakStatement {

    public GoBreakStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralIdentifier getLabel() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitBreakStatement(this);
    }
}
