package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.ASTBlock;
import com.intellij.formatting.Block;
import com.intellij.lang.ASTNode;
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

        return getASTElementType(((ASTBlock) block).getNode());
    }

    @Nullable
    public static IElementType getASTElementType(@Nullable ASTNode node) {
        if (node == null)
            return null;

        return node.getElementType();
    }

    @Nullable
    public static String getTextBetween(@Nullable Block firstBlock, @Nullable Block lastBlock) {
      ASTNode firstNode = null, lastNode = null;

      if ( firstBlock != null && firstBlock instanceof ASTBlock)
        firstNode = ((ASTBlock)firstBlock).getNode();

      if (lastBlock != null && lastBlock instanceof ASTBlock)
        lastNode = ((ASTBlock)lastBlock).getNode();

      return getTextBetween(firstNode, lastNode);
    }

    @Nullable
    public static String getTextBetween(@Nullable ASTNode firstNode, @Nullable ASTNode lastNode) {
        int firstBlockEnd = -1;
        int lastBlockStart = -1;

        PsiFile psiFile = null;
        if ( firstNode != null)
            psiFile = firstNode.getPsi().getContainingFile();

        if (psiFile == null && lastNode != null)
            psiFile = lastNode.getPsi().getContainingFile();

        if (psiFile == null)
            return null;

        if ( firstNode != null ) {
            firstBlockEnd = firstNode.getTextRange().getEndOffset();
            lastBlockStart = psiFile.getTextLength();
        } else {
            firstBlockEnd = 0;
        }

        if ( lastNode != null) {
            lastBlockStart = lastNode.getTextRange().getStartOffset();
        }

        return psiFile.getText().substring(firstBlockEnd, lastBlockStart);
    }

    public static int getLineCount(@Nullable String text) {
        if ( text == null )
            return Integer.MAX_VALUE;

        return StringUtil.countNewLines(text);
    }
}
