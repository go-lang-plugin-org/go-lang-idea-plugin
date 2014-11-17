package ro.redeul.google.go.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoParenthesizedExprOrType;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeParenthesized;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoParenthesisedExpressionOrTypeImpl extends GoPsiElementBase implements GoParenthesizedExprOrType {

    public GoParenthesisedExpressionOrTypeImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public GoType[] getType() {
        GoExpr innerExpr = getInnerExpression();
        return innerExpr != null  ? innerExpr.getType() : GoType.EMPTY_ARRAY;
    }

    @Override
    public boolean isConstantExpression() {
        GoExpr innerExpr = getInnerExpression();

        return innerExpr != null && innerExpr.isConstantExpression();
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitParenthesisedExprOrType(this);
    }

    @Override
    @Nullable
    public GoPsiType getInnerType() {
        GoParenthesisedExpressionOrTypeImpl childParenthesised = getInnerParenthesised();
        if ( childParenthesised != null ) {
            if ( childParenthesised.getInnerType() != null )
                return childParenthesised;

            return null;
        }

        return findChildByClass(GoPsiType.class);
    }

    @Override
    @Nullable
    public GoExpr getInnerExpression() {
        GoParenthesisedExpressionOrTypeImpl childParenthesised = getInnerParenthesised();
        if ( childParenthesised != null ) {
            if ( childParenthesised.getInnerExpression() != null )
                return childParenthesised;

            return null;
        }

        return findChildByClass(GoExpr.class);
    }

    public boolean isType() {
        return getInnerType() != null;
    }

    protected GoParenthesisedExpressionOrTypeImpl getInnerParenthesised() {
        return findChildByClass(GoParenthesisedExpressionOrTypeImpl.class);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }
}
