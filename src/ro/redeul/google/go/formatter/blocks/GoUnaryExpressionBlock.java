package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;

class GoUnaryExpressionBlock extends GoBlock {
    public GoUnaryExpressionBlock(ASTNode node, Alignment alignment, Indent indent, Wrap wrap,
                                  CommonCodeStyleSettings settings) {
        super(node, alignment, indent, wrap, settings);
    }

    @Override
    public Spacing getSpacing(Block child1, @NotNull Block child2) {
        return EMPTY_SPACING;
    }
}
