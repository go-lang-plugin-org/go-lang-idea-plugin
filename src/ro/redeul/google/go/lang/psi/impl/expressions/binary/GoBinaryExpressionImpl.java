package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.Operator;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public abstract class GoBinaryExpressionImpl<Op extends Operator> extends GoExpressionBase
        implements GoBinaryExpression<Op> {

    private Op[] operators;
    private TokenSet operatorTokens;

    GoBinaryExpressionImpl(@NotNull ASTNode node, @NotNull Op[] operators, @NotNull TokenSet operatorTokens) {
        super(node);
        this.operators = operators;
        this.operatorTokens = operatorTokens;
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    @Override
    public Op Op() {
        return GoPsiUtils.findOperator(this, operators, operatorTokens);
    }

    @Override
    public IElementType getOperator() {
        PsiElement child = findChildByFilter(GoElementTypes.OPS_BINARY);
        return child != null ? child.getNode().getElementType() : null;
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

    @Override
    protected GoType[] resolveTypes() {
        GoExpr leftOperand = getLeftOperand();
        GoExpr rightOperand = getRightOperand();

        if (leftOperand == null && rightOperand == null)
            return GoType.EMPTY_ARRAY;

        if (leftOperand == null)
            return rightOperand.getType();

        if (rightOperand == null)
            return leftOperand.getType();

        GoType[] leftTypes = leftOperand.getType();
        GoType[] rightTypes = rightOperand.getType();

        if (leftTypes.length == 1 && rightTypes.length == 1 && leftTypes[0] != null && rightTypes[0] != null) {
            if (leftTypes[0].isIdentical(rightTypes[0])) {
                return leftTypes;
            } else if (leftOperand.isConstantExpression()) {
                return rightTypes;
            } else if (rightOperand.isConstantExpression()) {
                return leftTypes;
            } else {
                return leftTypes;
            }

        }

        return GoType.EMPTY_ARRAY;
    }

    @Override
    public boolean isConstantExpression() {
        GoExpr leftOperand = getLeftOperand();
        GoExpr rightOperand = getRightOperand();

        return
                leftOperand != null && leftOperand.isConstantExpression() &&
                        rightOperand != null && rightOperand.isConstantExpression();
    }
}
