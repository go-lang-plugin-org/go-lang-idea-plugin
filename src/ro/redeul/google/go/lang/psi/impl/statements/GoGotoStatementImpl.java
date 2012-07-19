package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoContinueStatement;

public class GoGotoStatementImpl extends GoPsiElementBase
        implements GoContinueStatement {

    public GoGotoStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralIdentifier getLabel() {
        return findChildByClass(GoLiteralIdentifier.class);
    }
}
