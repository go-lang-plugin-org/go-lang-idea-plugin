package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;

class GoBinaryExpressionBlock extends GoBlock {
    private static final TokenSet EMPTY_SPACE_TOKENS = TokenSet.create(
            oMUL, oQUOTIENT, oREMAINDER,
            oSHIFT_LEFT, oSHIFT_RIGHT
    );

    private final Spacing spacing;

    public GoBinaryExpressionBlock(ASTNode node, Alignment alignment, Wrap wrap, CommonCodeStyleSettings settings) {
        super(node, alignment, Indent.getNormalIndent(), wrap, settings);

        GoBinaryExpression psi = node.getPsi(GoBinaryExpression.class);
        if (psi != null && EMPTY_SPACE_TOKENS.contains(psi.getOperator())) {
            spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
        } else {
            spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
        }
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        return isCommentBlock(child1) || isCommentBlock(child2) ? null : spacing;
    }

    @Override
    protected Indent getChildIndent(@Nullable PsiElement child) {
        return Indent.getNoneIndent();
    }
}
