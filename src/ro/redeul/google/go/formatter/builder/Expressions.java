package ro.redeul.google.go.formatter.builder;

import com.intellij.formatting.Block;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.formatter.blocks.*;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.*;

/**
 * <p/>
 * Created on Jan-13-2014 22:28
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
abstract class Expressions extends Literals {
    @Override
    public Block visitExpressionBinary(GoBinaryExpression<?> expression, State s) {
        return new ExpressionBinary(expression, s.settings, s.indent);
    }

    @Override
    public Block visitExpressionUnary(GoUnaryExpression expression, State s) {
        return new ExpressionBlock<GoUnaryExpression>(expression, s.settings, s.indent)
            .withCustomSpacing(GoBlockUtil.CustomSpacings.EXPR_UNARY);
    }

    @Override
    public Block visitExpressionSlice(GoSliceExpression expression, State s) {
        return new ExpressionSlice(expression, s.settings, s.indent);
    }

    @Override
    public Block visitExpressionSelector(GoSelectorExpression expression, State s) {
        return new ExpressionBlock<GoSelectorExpression>(expression, s.settings, s.indent)
            .withCustomSpacing(GoBlockUtil.CustomSpacings.EXPR_SELECTOR);
    }

    @Override
    public Block visitExpressionIndex(GoIndexExpression expression, State s) {
        return new ExpressionIndex(expression, s.settings, s.indent);
    }

    @Override
    public Block visitExpressionParenthesised(GoParenthesisedExpression expression, State s) {
        return new ExpressionBlock<GoParenthesisedExpression>(expression, s.settings, s.indent) {
            @Override
            protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
                childBlock = super.customizeBlock(childBlock, childPsi);

                if (childBlock instanceof ExpressionBlock) {
                    ((ExpressionBlock) childBlock).setDepth(1);
                }

                return childBlock;
            }
        }.withCustomSpacing(GoBlockUtil.CustomSpacings.EXPR_PARENTHESISED);
    }

    @Override
    public Block visitExpressionTypeAssertion(GoTypeAssertionExpression expression, State state) {
        return new ExpressionBlock<GoTypeAssertionExpression>(expression, state.settings, state.indent)
            .withCustomSpacing(GoBlockUtil.CustomSpacings.EXPR_TYPE_ASSERT);
    }

    @Override
    public Block visitExpressionCallOrConversion(GoCallOrConvExpression expression, State s) {
        return new ExpressionCallOrConversion(expression, s.settings, s.indent);
    }

    @Override
    public Block visitExpressionList(GoExpressionList expressions, State s) {
        return new ExpressionList(expressions, s.settings, s.indent);
    }
}
