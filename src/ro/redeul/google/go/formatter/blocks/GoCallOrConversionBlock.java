package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;

/**
* Call or conversion formatting block
 *
* <p/>
* Created on Jan-13-2014 01:05
*
* @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
*/
class GoCallOrConversionBlock extends GoExpressionBlock<GoCallOrConvExpression> {
    public GoCallOrConversionBlock(GoCallOrConvExpression psi, CommonCodeStyleSettings settings, Indent indent) {
        super(psi, settings, indent);

        setCustomSpacing(GoBlockUtil.CustomSpacings.CALL_OR_CONVERSION);
    }

    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        childBlock = super.customizeBlock(childBlock, childPsi);
        if ( getPsi().getArguments().length <= 1 )
            return childBlock;

        if ( childBlock instanceof GoExpressionBlock ) {
            ((GoExpressionBlock) childBlock).setDepth(myDepth + 1);
        }

        return childBlock;
    }
}
