package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;

/**
* Call or conversion formatting block
 *
* <p/>
* Created on Jan-13-2014 01:05
*
* @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
*/
public class ExpressionCallOrConversion extends ExpressionBlock<GoCallOrConvExpression> {

    int depthDelta = 0;

    public ExpressionCallOrConversion(GoCallOrConvExpression psi, CommonCodeStyleSettings settings, Indent indent) {
        super(psi, settings, indent);

        withCustomSpacing(GoBlockUtil.CustomSpacings.EXPR_CALL_OR_CONVERSION);
        setIndentedChildTokens(GoElementTypes.EXPRESSIONS);
        withDefaultSpacing(GoBlockUtil.Spacings.SPACE);

        int count = getPsi().getTypeArgument() != null ? 1 : 0;
        count += getPsi().getArguments().length;

        depthDelta = count > 1 ? 1 : 0;
    }

    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        childBlock = super.customizeBlock(childBlock, childPsi);
        if ( depthDelta == 0 )
            return childBlock;

        if ( childBlock instanceof ExpressionBlock) {
            ((ExpressionBlock) childBlock).setDepth(myDepth + 1);
        }

        return childBlock;
    }
}
