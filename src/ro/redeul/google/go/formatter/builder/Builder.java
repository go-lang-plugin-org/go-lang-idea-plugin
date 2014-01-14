package ro.redeul.google.go.formatter.builder;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.java.LeafBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.formatter.TokenSets;
import ro.redeul.google.go.formatter.blocks.CommentGroupPart;
import ro.redeul.google.go.formatter.blocks.Leaf;
import ro.redeul.google.go.lang.psi.GoPsiElement;

import java.util.Map;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Alignments;
import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.Indents;

/**
 * <p/>
 * Created on Jan-13-2014 22:08
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class Builder extends Others {

    final static Builder builder = new Builder();


    public static Block make(ASTNode node, CommonCodeStyleSettings settings) {
        return make(node, settings,
            Indents.NONE, Alignments.NONE, Alignments.EMPTY_MAP, false);
    }

    public static Block make(ASTNode node, CommonCodeStyleSettings settings,
                             @NotNull Alignment alignment) {
        return make(node, settings, Indents.NONE, alignment, Alignments.EMPTY_MAP, false);
    }

    public static Block make(ASTNode node, CommonCodeStyleSettings styleSettings, Indent indent) {
        return make(node, styleSettings, indent, Alignments.NONE, Alignments.EMPTY_MAP, false);
    }

    public static Block make(ASTNode node, CommonCodeStyleSettings settings,
                             @Nullable Indent indent, @Nullable Alignment alignment,
                             @NotNull Map<Alignments.Key, Alignment> alignmentsMap,
                             boolean isPartOfLeadingCommentGroup) {

        PsiElement psi = node.getPsi();

        if (psi instanceof GoPsiElement) {
            Block block = ((GoPsiElement) psi).accept(builder, new State(settings, indent, alignment, alignmentsMap));

            if (block != null)
                return block;
        }

        if (psi instanceof PsiComment && isPartOfLeadingCommentGroup)
            return new CommentGroupPart((PsiComment) psi, indent, alignment);

        IElementType elementType = node.getElementType();
        if (TokenSets.LEAF_BLOCKS.contains(elementType))
            return new Leaf(node, indent, alignment);

        return new Leaf(node, indent, alignment);
    }
}
