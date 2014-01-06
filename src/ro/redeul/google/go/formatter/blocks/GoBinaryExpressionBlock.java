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
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;

class GoBinaryExpressionBlock extends GoBlock {

    private Spacing spacing;

    private static final TokenSet EMPTY_SET = TokenSet.create(
            oMINUS, oPLUS, oMUL, oQUOTIENT, oREMAINDER,
            oBIT_AND, oBIT_CLEAR, oBIT_XOR,
            oSHIFT_LEFT, oSHIFT_RIGHT
    );

    public GoBinaryExpressionBlock(ASTNode node, Alignment alignment, Wrap wrap, CommonCodeStyleSettings settings) {
        super(node, alignment, Indent.getNormalIndent(), wrap, settings);

        GoBinaryExpression psi = node.getPsi(GoBinaryExpression.class);

        if (psi == null) {
            spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
            return;
        }

        ASTNode parentElement = node.getTreeParent();
        ASTNode preParentElement = node;
        IElementType parentElementType = parentElement.getElementType();

        boolean inARelation = false;
        if (parentElementType == REL_EXPRESSION) {
            inARelation = true;
        }

        while (parentElementType != BUILTIN_CALL_EXPRESSION
                && parentElementType != EXPRESSION_LIST
                && !STMTS.contains(parentElementType)
                && parentElementType != CONST_DECLARATION
                && parentElementType != VAR_DECLARATION
                ) {
            preParentElement = parentElement;
            parentElement = parentElement.getTreeParent();

            if (parentElement == null) {
                return;
            }

            parentElementType = parentElement.getElementType();

            if (parentElementType == REL_EXPRESSION) {
                inARelation = true;
            }
        }

        if (inARelation) {
            spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
            return;
        }

        if (parentElementType == EXPRESSION_LIST) {
            if (inTheSameLine(psi.getLeftOperand().getNode(), psi.getRightOperand().getNode())
                && !(node.getElementType() == LOG_OR_EXPRESSION || node.getElementType() == LOG_AND_EXPRESSION || node.getElementType() == REL_EXPRESSION)
                ) {
                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
            } else {
                spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
            }
            return;
        }

        if (STMTS.contains(parentElementType)
                || parentElementType == CONST_DECLARATION
                || parentElementType == VAR_DECLARATION
            ) {
            try {
                if (preParentElement.getElementType() == ADD_EXPRESSION
                        && node != preParentElement
                        && preParentElement.getPsi(GoAdditiveExpression.class).getOperator() == oBIT_OR) {
                    spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
                    return;
                }
            } catch (NullPointerException ignored) {

            }

            if (preParentElement.getElementType() == SLICE_EXPRESSION) {
                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
                return;
            }

            spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
            return;
        }

        ASTNode expression = node;
        ASTNode expressionChild = node;

        while (expression.getElementType() != BUILTIN_CALL_EXPRESSION
                ) {
            expressionChild = expression;
            expression = expression.getTreeParent();
        }

        if (expression.getElementType() != BUILTIN_CALL_EXPRESSION) {
            spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
            return;
        }

        ASTNode elem = expressionChild;
        boolean isAlone = true;
        while (!elem.getElementType().toString().equals(")")) {
            elem = elem.getTreeNext();
            if (elem.getElementType().toString().equals(",")) {
                isAlone = false;
            }
        }

        if (!isAlone) {
            if (inTheSameLine(psi.getLeftOperand().getNode(), psi.getRightOperand().getNode())) {
                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
            } else {
                spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
            }
            return;
        }

        elem = expressionChild;
        while (!elem.getElementType().toString().equals("(")) {
            elem = elem.getTreePrev();
            if (elem.getElementType().toString().equals(",")) {
                isAlone = false;
            }
        }

        if (!isAlone) {
            if (inTheSameLine(node, elem)) {
                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
            } else {
                spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
            }
            return;
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
