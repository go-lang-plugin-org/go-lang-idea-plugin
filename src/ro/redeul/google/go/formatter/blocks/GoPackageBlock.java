package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

class GoPackageBlock extends GoBlock {


    public GoPackageBlock(ASTNode node, Alignment alignment, Indent indent,
                          Wrap wrap, CommonCodeStyleSettings settings) {
        super(node, alignment, indent, wrap, settings);
    }

    @Override
    public Spacing getSpacing(Block child1, Block child2) {
        if ( ((GoBlock)child1).getNode().getElementType() == GoTokenTypes.kPACKAGE )  {
            return BASIC_SPACING;
        }

        return EMPTY_SPACING;
    }
}
