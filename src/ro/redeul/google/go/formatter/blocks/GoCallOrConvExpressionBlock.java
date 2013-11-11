package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

class GoCallOrConvExpressionBlock extends GoBlock {
    public GoCallOrConvExpressionBlock(ASTNode node, Alignment alignment, Indent indent, Wrap wrap,
                                       CommonCodeStyleSettings settings) {
        super(node, alignment, indent, wrap, settings);
    }

    @Override
    protected Indent getChildIndent(@Nullable PsiElement child) {
        if (child instanceof GoExpressionList || child instanceof GoLiteralIdentifier) {
            return CONTINUATION_WITHOUT_FIRST;
        }
        return super.getChildIndent(child);
    }

    @Override
    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
        IElementType child1Type = child1.getNode().getElementType();
        IElementType child2Type = child2.getNode().getElementType();
        if (child1Type == oCOMMA) {
            return BASIC_SPACING_KEEP_LINE_BREAKS;
        }

        if (child1Type == GoTokenTypes.pLPAREN && child2Type == GoTokenTypes.pRPAREN)
            return GoBlock.EMPTY_SPACING;

        return EMPTY_SPACING_KEEP_LINE_BREAKS;
    }
}
