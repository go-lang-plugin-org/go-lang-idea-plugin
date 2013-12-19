package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;

class GoExpressionListBlock extends GoBlock {
    public GoExpressionListBlock(ASTNode node, Alignment alignment, Indent indent, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, null, settings);
    }

    @Override
    protected Indent getChildIndent(PsiElement child) {
        return Indent.getNoneIndent();
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        IElementType child1Type = child1.getNode().getElementType();
        //IElementType child2Type = child2.getNode().getElementType();
        if (child1Type == oCOMMA) {
            return BASIC_SPACING_KEEP_LINE_BREAKS;
        }

        return EMPTY_SPACING;
    }
}
