package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNewLineNode;

class GoTypeStructFieldBlock extends GoBlock {
    private static final TokenSet FIELD_TYPE_SET = TokenSet.create(
        TYPE_SLICE,
        TYPE_NAME,
        TYPE_INTERFACE,
        TYPE_CHAN_BIDIRECTIONAL,
        TYPE_CHAN_RECEIVING,
        TYPE_CHAN_SENDING
    );

    private final Alignment fieldTypeAlignment;

    public GoTypeStructFieldBlock(ASTNode node, Alignment fieldTypeAlignment, Indent indent, CommonCodeStyleSettings settings) {
        super(node, null, indent, null, settings);
        this.fieldTypeAlignment = fieldTypeAlignment;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> children = new ArrayList<Block>();
        for (ASTNode child : getGoChildren()) {
            if (isNewLineNode(child.getPsi())) {
                continue;
            }

            Block block;
            Indent indent = Indent.getNormalIndent();
            if (FIELD_TYPE_SET.contains(child.getElementType())) {
                block = GoBlockGenerator.generateBlock(child, indent, fieldTypeAlignment, mySettings);
            } else {
                block = GoBlockGenerator.generateBlock(child, indent, mySettings);
            }
            children.add(block);
        }
        return children;
    }
}
