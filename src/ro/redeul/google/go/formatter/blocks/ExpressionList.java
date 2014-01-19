package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacings;

public class ExpressionList extends Code<GoExpressionList> {

    int depth = 1;

    public ExpressionList(GoExpressionList psi, CommonCodeStyleSettings settings, Indent indent) {
        super(psi, settings, indent);

        withCustomSpacing(CustomSpacings.NO_SPACE_BEFORE_COMMA);
        withDefaultSpacing(GoBlockUtil.Spacings.SPACE);
        setDepth(1);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        childBlock = super.customizeBlock(childBlock, childPsi);
        if (childBlock instanceof ExpressionBlock)
            ((ExpressionBlock) childBlock).setDepth(depth);

        return childBlock;
    }
}