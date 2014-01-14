package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoExpressionListImpl extends GoPsiElementBase
    implements GoExpressionList {

    public GoExpressionListImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr[] getExpressions() {
        return getElements();
    }

    @Override
    public GoExpr[] getElements() {
        return findChildrenByClass(GoExpr.class);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitExpressionList(this, data);
    }
}
