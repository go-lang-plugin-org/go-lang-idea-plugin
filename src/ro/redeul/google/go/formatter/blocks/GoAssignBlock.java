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

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

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
            if (isWhiteSpaceNode(child.getPsi())) {
                continue;
            }

//            if (child.getElementType() == GoElementTypes.oASSIGN) {
//                block = GoBlocks.generateBlock(child, indent, assignAlignment, mySettings);
//            } else {
                block = GoBlocks.generate(child, mySettings, indent);
//            }
            children.add(block);
        }
        return children;
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        IElementType type1 = child1.getNode().getElementType();
        IElementType type2 = child2.getNode().getElementType();
        if (type1 == GoElementTypes.oASSIGN || type2 == GoElementTypes.oASSIGN) {
            return BASIC_SPACING;
        }

        return super.getGoBlockSpacing(child1, child2);
    }
}
