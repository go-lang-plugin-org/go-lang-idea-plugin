package ro.redeul.google.go.formatter.builder;

import com.intellij.formatting.Block;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.formatter.blocks.*;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIndexExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSliceExpression;

/**
 * <p/>
 * Created on Jan-13-2014 22:28
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
abstract class Expressions extends Types {
    @Override
    public Block visitExpressionBinary(GoBinaryExpression<?> expression, State s) {
        return new ExpressionBinary(expression, s.settings, s.indent);
    }

    @Override
    public Block visitExpressionUnary(GoUnaryExpression expression, State s) {
        return new ExpressionBlock<GoUnaryExpression>(expression, s.settings, s.indent)
            .setCustomSpacing(GoBlockUtil.CustomSpacings.UNARY_EXPRESSION);
    }

    @Override
    public Block visitExpressionSlice(GoSliceExpression expression, State s) {
        return new ExpressionSlice(expression, s.settings, s.indent);
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

        };
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
