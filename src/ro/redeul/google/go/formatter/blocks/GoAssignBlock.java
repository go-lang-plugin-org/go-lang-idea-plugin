package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;

import java.util.ArrayList;
import java.util.List;

class GoAssignBlock extends GoBlock {
    private final Alignment assignAlignment;

    public GoAssignBlock(ASTNode node, Alignment assignAlignment, Indent indent, CommonCodeStyleSettings settings) {
        super(node, null, indent, null, settings);
        this.assignAlignment = assignAlignment;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> children = new ArrayList<Block>();
        for (ASTNode child : getGoChildren()) {
            Block block;
            Indent indent = Indent.getNormalIndent();
            if (child.getElementType() == GoElementTypes.oASSIGN) {
                block = GoBlockGenerator.generateBlock(child, indent, assignAlignment, mySettings);
            } else {
                block = GoBlockGenerator.generateBlock(child, indent, mySettings);
            }
            children.add(block);
        }
        return children;
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        IElementType type2 = child2.getNode().getElementType();
        if (type2 == GoElementTypes.oASSIGN) {
            return BASIC_SPACING;
        }

        return null;
    }
}
