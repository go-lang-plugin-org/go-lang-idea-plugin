package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNewLineNode;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

class GoTypeStructFieldBlock extends GoBlock {
    private static final TokenSet FIELD_TYPE_SET = TokenSet.create(
        TYPE_SLICE,
        TYPE_NAME,
        TYPE_INTERFACE,
        TYPE_CHAN_BIDIRECTIONAL,
        TYPE_CHAN_RECEIVING,
        TYPE_CHAN_SENDING,
        TYPE_STRUCT
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
            if (isNewLineNode(child.getPsi()) || isWhiteSpaceNode(child.getPsi())) {
                continue;
            }

            Block block;
            Indent indent = Indent.getNormalIndent(false);
            if (FIELD_TYPE_SET.contains(child.getElementType())) {
                block = GoBlockGenerator.generateBlock(child, Indent.getNoneIndent(), fieldTypeAlignment, mySettings);
            } else {
                block = GoBlockGenerator.generateBlock(child, null, null, mySettings);
            }
            children.add(block);
        }
        return children;
    }

    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2) {
        return Spacing.createSpacing(1, 10, 0, false, 0);
    }
}
