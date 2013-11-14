package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;

class GoBinaryExpressionBlock extends GoBlock {
    public enum Mode {
    }

    private static final TokenSet EMPTY_SPACE_TOKENS = TokenSet.create(
            oQUOTIENT, oREMAINDER, oSHIFT_LEFT, oSHIFT_RIGHT
    );

    private final Spacing spacing;

    public GoBinaryExpressionBlock(ASTNode node, Alignment alignment, Wrap wrap, CommonCodeStyleSettings settings) {
        super(node, alignment, Indent.getNormalIndent(), wrap, settings);

        GoBinaryExpression psi = node.getPsi(GoBinaryExpression.class);
        if (psi != null) {
            IElementType psiOperator = psi.getOperator();

            if (EMPTY_SPACE_TOKENS.contains(psiOperator)) {
                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
                return;
            }
            IElementType parentElementType = node.getTreeParent().getElementType();
            if (EXPRESSION_SETS.contains(parentElementType)
                    && inTheSameLine(psi.getLeftOperand().getNode(), psi.getRightOperand().getNode())
            && (
                    psiOperator == oMUL ||
                    psiOperator == oQUOTIENT ||
                    psiOperator == oPLUS ||
                    psiOperator == oMINUS
            )) {
                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
                return;
            }
        }

        spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        return isCommentBlock(child1) || isCommentBlock(child2) ? BASIC_SPACING_KEEP_LINE_BREAKS : spacing;
    }

    @Override
    protected Indent getChildIndent(@Nullable PsiElement child) {
        return null;
    }
}
