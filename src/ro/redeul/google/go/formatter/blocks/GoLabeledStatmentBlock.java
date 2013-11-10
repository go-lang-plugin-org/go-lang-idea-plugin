package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;

class GoLabeledStatmentBlock extends GoBlock {
    public GoLabeledStatmentBlock(ASTNode node, CommonCodeStyleSettings styleSettings) {
        super(node, null, Indent.getLabelIndent(), null, styleSettings);
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        // There should be no space between label identifier and ':'
        if (child2.getNode().getElementType() == oCOLON) {
            return EMPTY_SPACING;
        }

        // The statement after ':' should be in the next line
        if (child1.getNode().getElementType() == oCOLON) {
            return ONE_LINE_SPACING;
        }

        return super.getGoBlockSpacing(child1, child2);
    }
}
