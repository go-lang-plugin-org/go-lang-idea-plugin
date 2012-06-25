package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression;
import ro.redeul.google.go.lang.psi.types.GoType;

public class GoRelationalExpressionImpl extends GoBinaryExpressionImpl
    implements GoRelationalExpression
{
    public GoRelationalExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType resolveType() {
        GoExpr operand = getLeftOperand();
        return operand != null ? operand.getType() : null;
    }

    @Override
    public IElementType getOperator() {
        PsiElement child = findChildByFilter(GoElementTypes.RELATIONAL_OPS);
        return child != null ? child.getNode().getElementType(): null;
    }

}

