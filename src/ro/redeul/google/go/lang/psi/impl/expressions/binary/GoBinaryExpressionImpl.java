/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;

/**
 * // TODO: Explain yourself.
 *
 * @author Mihai Claudiu Toader <mtoader@midokura.com>
 *         Date: 6/6/12
 */
public abstract class GoBinaryExpressionImpl extends GoExpressionBase
    implements GoBinaryExpression {

    public GoBinaryExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public IElementType getOperator() {
        PsiElement child = findChildByFilter(GoElementTypes.BINARY_OPS);
        return child != null ? child.getNode().getElementType(): null;
    }

    @Override
    @Nullable
    public GoExpr getLeftOperand() {
        GoExpr[] children = findChildrenByClass(GoExpr.class);
        return children.length == 0 ? null : children[0];
    }

    @Override
    public GoExpr getRightOperand() {
        GoExpr[] children = findChildrenByClass(GoExpr.class);
        return children.length <= 1 ? null : children[1];
    }
}
