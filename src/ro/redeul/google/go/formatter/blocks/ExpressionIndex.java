package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIndexExpression;

/**
* TODO: Document this
* <p/>
* Created on Jan-13-2014 22:01
*
* @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
*/
public class ExpressionIndex extends ExpressionBlock<GoIndexExpression> {

    public ExpressionIndex(GoIndexExpression psi, CommonCodeStyleSettings settings, Indent indent) {
        super(psi, settings, indent);
    }

    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        childBlock = super.customizeBlock(childBlock, childPsi);

        if (childBlock instanceof ExpressionBlock) {
            ExpressionBlock child = (ExpressionBlock) childBlock;

            if (childPsi == getPsi().getIndex()) {
                child.setDepth(myDepth + 1);
            } else if (childPsi == getPsi().getBaseExpression()) {
                child.setDepth(1);
            }
        }

        return childBlock;
    }
}
