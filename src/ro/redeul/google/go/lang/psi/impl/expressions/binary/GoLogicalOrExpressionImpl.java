package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalOrExpression;
import ro.redeul.google.go.lang.psi.types.GoType;

public class GoLogicalOrExpressionImpl extends GoBinaryExpressionImpl
    implements GoLogicalOrExpression
{
    public GoLogicalOrExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        GoExpr operand = getLeftOperand();
        // return Undefined
        return operand != null ? operand.getType() : null;
    }
}

