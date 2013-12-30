package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.ASTBlock;
import com.intellij.formatting.Block;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for formatter block handling.
 *
 * @author Mihai Toader <mtoader@gmail.com>
 */
public class GoFormatterUtil {

    @Nullable
    public static IElementType getASTElementType(@Nullable Block block) {
        if (block == null || !(block instanceof ASTBlock))
            return null;

        return ((ASTBlock) block).getNode().getElementType();
    }

    @Nullable
    public static String getTextBetween(@Nullable Block firstBlock, @Nullable Block lastBlock) {
        int firstBlockEnd = -1;
        int lastBlockStart = -1;

        PsiFile psiFile = null;
        if ( firstBlock != null && firstBlock instanceof ASTBlock)
            psiFile = ((ASTBlock)firstBlock).getNode().getPsi().getContainingFile();

        if (psiFile == null && lastBlock != null && lastBlock instanceof ASTBlock)
            psiFile = ((ASTBlock)lastBlock).getNode().getPsi().getContainingFile();

        if (psiFile == null)
            return null;

        if ( firstBlock != null ) {
            firstBlockEnd = firstBlock.getTextRange().getEndOffset();
            lastBlockStart = psiFile.getTextLength();
        } else {
            firstBlockEnd = 0;
        }

        if ( lastBlock != null) {
            lastBlockStart = lastBlock.getTextRange().getStartOffset();
        }

        return psiFile.getText().substring(firstBlockEnd, lastBlockStart);
    }

    public static int getLineCount(@Nullable String text) {
        if ( text == null )
            return Integer.MAX_VALUE;

        return StringUtil.countNewLines(text);
    }
}
