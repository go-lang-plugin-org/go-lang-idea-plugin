package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSliceExpression;

import static ro.redeul.google.go.formatter.GoFormatterUtil.getASTElementType;
import static ro.redeul.google.go.formatter.GoFormatterUtil.getPsiElement;

/**
 * <p/>
 * Created on Jan-13-2014 21:58
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class ExpressionSlice extends ExpressionBlock<GoSliceExpression> {

    public ExpressionSlice(GoSliceExpression slice, CommonCodeStyleSettings settings, Indent indent) {
        super(slice, settings, indent);
    }

    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        childBlock = super.customizeBlock(childBlock, childPsi);

        if (childBlock instanceof ExpressionBlock) {
            ExpressionBlock child = (ExpressionBlock) childBlock;

            if (childPsi == getPsi().getBaseExpression()) {
                child.setDepth(1);
            } else if (childPsi == getPsi().getFirstIndex()) {
                child.setDepth(myDepth + 1);
            } else if (childPsi == getPsi().getSecondIndex()) {
                child.setDepth(myDepth + 1);
            } else if (childPsi == getPsi().getCapacity()) {
                child.setDepth(myDepth + 1);
            }
        }

        return childBlock;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block block1, @NotNull Block block2) {
        IElementType typeChild1 = getASTElementType(block1);
        IElementType typeChild2 = getASTElementType(block2);

        GoExpr child1Expr = getPsiElement(block1, GoExpr.class);
        if (child1Expr != null && typeChild2 == oCOLON) {
            if (child1Expr == getPsi().getFirstIndex() && getPsi().getSecondIndex() != null &&
                (isBinary(child1Expr) || isBinary(getPsi().getSecondIndex())))
                return GoBlockUtil.Spacings.SPACE;

            if (child1Expr == getPsi().getSecondIndex() && getPsi().getCapacity() != null &&
                (isBinary(child1Expr) || isBinary(getPsi().getCapacity())))
                return GoBlockUtil.Spacings.SPACE;

        }

        GoExpr child2Expr = getPsiElement(block2, GoExpr.class);
        if (typeChild1 == oCOLON && child2Expr != null) {
            if (getPsi().getFirstIndex() != null && child2Expr == getPsi().getSecondIndex() &&
                (isBinary(getPsi().getFirstIndex()) || isBinary(child2Expr)))
                return GoBlockUtil.Spacings.SPACE;

            if (getPsi().getSecondIndex() != null && child2Expr == getPsi().getCapacity() &&
                (isBinary(getPsi().getSecondIndex()) || isBinary(child2Expr)))
                return GoBlockUtil.Spacings.SPACE;
        }

        return GoBlockUtil.Spacings.NONE;
    }

    private boolean isBinary(@Nullable GoExpr expr) {
        return expr != null && expr instanceof GoBinaryExpression<?>;
    }

    @Override
    public void setDepth(int depth) {
        super.setDepth(depth);

        if (depth <= 1) {
            withCustomSpacing(GoBlockUtil.CustomSpacings.EXPR_SLICE_EXPANDED);
        }
    }
}
