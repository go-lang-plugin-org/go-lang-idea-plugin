package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoForStatement;

public class GoForStatementImpl extends GoPsiElementBase
    implements GoForStatement {

    public GoForStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoBlockStatement getBlock() {
        return findChildByClass(GoBlockStatement.class);
    }
}
