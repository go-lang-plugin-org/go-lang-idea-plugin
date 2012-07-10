package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import ro.redeul.google.go.lang.parser.GoElementTypes;

public class GoFunctionDeclarationBlock extends GoBlock {
    public GoFunctionDeclarationBlock(ASTNode node, Alignment alignment, Indent indent,
                                      CommonCodeStyleSettings settings) {
        super(node, alignment, indent, null, settings);
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        if (child1.getNode().getElementType() == GoElementTypes.pRPAREN) {
            return BASIC_SPACING;
        }

        if (child1.getNode().getElementType() == GoElementTypes.FUNCTION_RESULT) {
            return BASIC_SPACING;
        }

        Spacing spacing = super.getGoBlockSpacing(child1, child2);
        if (spacing != null) {
            return spacing;
        }

        return EMPTY_SPACING;
    }
}
