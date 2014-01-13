package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;

/**
 * Base expression block. Knows about depth of the current expression and it will propagate it to all the childrens.
 *
 * <p/>
 * Created on Jan-09-2014 10:21
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoExpressionBlock<Expression extends GoExpr> extends GoSyntheticBlock<Expression> {

    int myDepth = 1;

    public GoExpressionBlock(@NotNull Expression node, CommonCodeStyleSettings settings,
                             Indent indent) {
        super(node, settings, indent, null, GoBlockUtil.Alignments.EMPTY_MAP);
        setDepth(1);
    }

    public void setDepth(int depth) {
        this.myDepth = depth;
    }

    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        if ( childBlock instanceof GoExpressionBlock<?> ) {
            ((GoExpressionBlock) childBlock).setDepth(myDepth);
        }

        return super.customizeBlock(childBlock, childPsi);
    }
}
