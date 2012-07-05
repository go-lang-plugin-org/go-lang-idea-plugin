package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;

class GoCallOrConvExpressionBlock extends GoBlock {
    public GoCallOrConvExpressionBlock(ASTNode node, Alignment alignment, Indent indent, Wrap wrap,
                                       CommonCodeStyleSettings settings) {
        super(node, alignment, indent, wrap, settings);
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        IElementType child1Type = child1.getNode().getElementType();
        IElementType child2Type = child2.getNode().getElementType();
        if (child2Type == EXPRESSION_LIST ||
            child1Type == EXPRESSION_LIST ||
            child1Type == pLPAREN && child2Type != pRPAREN) {
            return EMPTY_SPACING_KEEP_LINE_BREAKS;
        }
        return EMPTY_SPACING;
    }
}
