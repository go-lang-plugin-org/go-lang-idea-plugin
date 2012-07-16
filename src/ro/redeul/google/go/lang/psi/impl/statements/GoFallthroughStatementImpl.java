package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoFallthroughStatement;

public class GoFallthroughStatementImpl extends GoPsiElementBase implements GoFallthroughStatement {
    public GoFallthroughStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
