package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;

public class GoReturnStatementImpl extends GoPsiElementBase
    implements GoReturnStatement {
    public GoReturnStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr[] getExpressions() {
        return findChildrenByClass(GoExpr.class);
    }
}
