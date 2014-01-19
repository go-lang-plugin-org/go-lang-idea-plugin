package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoDocumentedPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoFallthroughStatement;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoFallthroughStatementImpl extends GoDocumentedPsiElementBase implements GoFallthroughStatement {

    public GoFallthroughStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementFallthrough(this, data);
    }
}
