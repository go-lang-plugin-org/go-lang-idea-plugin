package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoForWithConditionStatement;

public class GoForWithConditionStatementImpl extends GoPsiElementBase
    implements GoForWithConditionStatement {
    public GoForWithConditionStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getCondition() {
        return findChildByClass(GoExpr.class);
    }
}
