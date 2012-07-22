package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;

public class GoExpressionListImpl extends GoPsiElementBase
    implements GoExpressionList {

    public GoExpressionListImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr[] getExpressions() {
        return findChildrenByClass(GoExpr.class);
    }
}
