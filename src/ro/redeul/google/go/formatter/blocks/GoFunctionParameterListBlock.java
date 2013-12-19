/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.Nullable;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
class GoFunctionParameterListBlock extends GoBlock {
    public GoFunctionParameterListBlock(ASTNode node, Indent indent, CommonCodeStyleSettings settings) {
        super(node, null, indent, null, settings);
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        //boolean isNewLine1 = GoPsiUtils.isNewLineNode(child1.getNode().getPsi());
        //boolean isNewLine2 = GoPsiUtils.isNewLineNode(child2.getNode().getPsi());

        //String text1 = child1.getNode().getText();
        //String text2 = child2.getNode().getText();

        return super.getGoBlockSpacing(child1, child2);
    }

    @Nullable
    @Override
    protected Indent getChildIndent(@Nullable PsiElement prevChild, @Nullable PsiElement child) {
        if (prevChild != null && child != null && !inTheSameLine(prevChild.getNode(), child.getNode()))
            return Indent.getNormalIndent();

        return super.getChildIndent(prevChild, child);
    }
}
