package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;

class GoFunctionDeclarationBlock extends GoBlock {
    public GoFunctionDeclarationBlock(ASTNode node, Alignment alignment, Indent indent,
                                      CommonCodeStyleSettings settings) {
        super(node, alignment, indent, null, settings);
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        IElementType child1ElementType = child1.getNode().getElementType();
        IElementType child2ElementType = child2.getNode().getElementType();

        if (child1ElementType == GoElementTypes.pRPAREN) {
            return BASIC_SPACING;
        }

        if (child1ElementType == GoElementTypes.FUNCTION_RESULT) {
            return BASIC_SPACING;
        }

        Spacing spacing = super.getGoBlockSpacing(child1, child2);
        if (spacing != null) {
            return spacing;
        }

        return EMPTY_SPACING;
    }
}
