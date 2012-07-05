package ro.redeul.google.go.formatter.blocks;

import java.util.List;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.Nullable;

class GoLeafBlock extends GoBlock {
    public GoLeafBlock(ASTNode node, Alignment alignment, Indent indent,
                       Wrap wrap, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, wrap, settings);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Nullable
    @Override
    protected List<Block> buildChildren() {
        return null;
    }
}
