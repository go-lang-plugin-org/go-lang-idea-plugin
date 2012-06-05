/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.types.GoType;

public class GoAdditiveExpressionImpl extends GoBinaryExpressionImpl implements
                                                               GoAdditiveExpression {
    public GoAdditiveExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType resolveType() {
        GoExpr operand = getLeftOperand();
        return operand != null ? operand.getType() : null;
    }
}

