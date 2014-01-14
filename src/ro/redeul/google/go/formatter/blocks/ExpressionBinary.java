package ro.redeul.google.go.formatter.blocks;

import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.Operator;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoMultiplicativeExpression;

import java.util.List;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacing;

public class ExpressionBinary extends ExpressionBlock<GoBinaryExpression<?>> {

    int cutoff = 0;

    private final CustomSpacing COMPACT_MODE_SPACING =
        CustomSpacing.Builder()
            .setNone(EXPRESSIONS, OPS_ADD)
            .setNone(OPS_ADD, EXPRESSIONS)
            .setNone(EXPRESSIONS, OPS_MUL)
            .setNone(OPS_MUL, EXPRESSIONS)
            .build();

    public ExpressionBinary(@NotNull GoBinaryExpression node, CommonCodeStyleSettings settings,
                            Indent indent) {
        super(node, settings, indent);
    }

    protected void setCutoff(int cutoff) {
        this.cutoff = cutoff;
        if (getPsi().Op().precedence() >= cutoff)
            setCustomSpacing(COMPACT_MODE_SPACING);
    }

    @Nullable
    @Override
    protected List<Block> buildChildren() {
        if ( cutoff == 0 )
            setCutoff(findCutoff(getPsi(), myDepth));

        return super.buildChildren();
    }

    @Override
    protected Block customizeBlock(@NotNull Block childBlock, @NotNull PsiElement childPsi) {
        if (childBlock instanceof ExpressionBinary) {
            ((ExpressionBinary)childBlock).setCutoff(cutoff);
        }

        return super.customizeBlock(childBlock, childPsi);
    }

    private class BinaryExpressionInfo {
        boolean has4 = false, has5 = false;
        int maxProblem = 0;
    }

    private int findCutoff(GoBinaryExpression expr, int depth) {
        BinaryExpressionInfo expressionInfo = walkBinary(expr);
        if (expressionInfo.maxProblem > 0) {
            return expressionInfo.maxProblem + 1;
        }
        if (expressionInfo.has4 && expressionInfo.has5) {
            if (depth == 1) {
                return 5;
            }
            return 4;
        }

        if (depth == 1) {
            return 6;
        }

        return 4;
    }

    private BinaryExpressionInfo walkBinary(GoBinaryExpression expr) {

        BinaryExpressionInfo info = new BinaryExpressionInfo();

        Operator exprOp = expr.Op();
        switch (exprOp.precedence()) {
            case 4:
                info.has4 = true;
                break;
            case 5:
                info.has5 = true;
                break;
        }

        // switch l := e.X.(type) {

        /*
        case *ast.BinaryExpr:
            if l.Op.Precedence() < e.Op.Precedence() {
                // parens will be inserted.
                // pretend this is an *ast.ParenExpr and do nothing.
                break
            }
            h4, h5, mp := walkBinary(l)
            has4 = has4 || h4
            has5 = has5 || h5
            if maxProblem < mp {
                maxProblem = mp
            }
        */
        if (expr.getLeftOperand() instanceof GoBinaryExpression) {
            GoBinaryExpression left = (GoBinaryExpression) expr.getLeftOperand();

            if (!(left.Op().precedence() < exprOp.precedence())) {
                BinaryExpressionInfo leftInfo = walkBinary(left);
                info.has4 = info.has4 || leftInfo.has4;
                info.has5 = info.has5 || leftInfo.has5;

                if (info.maxProblem < leftInfo.maxProblem)
                    info.maxProblem = leftInfo.maxProblem;
            }
        }

        // }

        // switch r := e.Y.(type) {

/*
            case *ast.BinaryExpr:
            if r.Op.Precedence() <= e.Op.Precedence() {
                // parens will be inserted.
                // pretend this is an *ast.ParenExpr and do nothing.
                break
            }
            h4, h5, mp := walkBinary(r)
            has4 = has4 || h4
            has5 = has5 || h5
            if maxProblem < mp {
                maxProblem = mp
            }
*/
        if (expr.getRightOperand() instanceof GoBinaryExpression) {
            GoBinaryExpression right = (GoBinaryExpression) expr.getRightOperand();

            if (!(right.Op().precedence() <= exprOp.precedence())) {
                BinaryExpressionInfo rightInfo = walkBinary(right);
                info.has4 = info.has4 || rightInfo.has4;
                info.has5 = info.has5 || rightInfo.has5;

                if (info.maxProblem < rightInfo.maxProblem)
                    info.maxProblem = rightInfo.maxProblem;
            }
        }

        // TODO: handle this case
/*
            case *ast.StarExpr:
            if e.Op == token.QUO { // `*//*
    `
                maxProblem = 5
            }
*/

/*
            case *ast.UnaryExpr:
            switch e.Op.String() + r.Op.String() {
                case "/*", "&&", "&^":
                    maxProblem = 5
                case "++", "--":
                    if maxProblem < 4 {
                    maxProblem = 4
                }
            }
*/
        if (expr.getRightOperand() instanceof GoUnaryExpression &&
            !(expr.getRightOperand() instanceof GoPrimaryExpression)) {
            GoUnaryExpression rightExpr = (GoUnaryExpression) expr.getRightOperand();

            GoUnaryExpression.Op rightOp = rightExpr.getOp();
            if (
                (exprOp == GoMultiplicativeExpression.Op.Quotient && rightOp == GoUnaryExpression.Op.Pointer) ||
                (exprOp == GoMultiplicativeExpression.Op.BitAnd && rightOp == GoUnaryExpression.Op.Address) ||
                (exprOp == GoMultiplicativeExpression.Op.BitAnd && rightOp == GoUnaryExpression.Op.Not)) {
                info.maxProblem = 5;
            } else if (
                (exprOp == GoAdditiveExpression.Op.Plus && rightOp == GoUnaryExpression.Op.Plus) ||
                (exprOp == GoAdditiveExpression.Op.Minus && rightOp == GoUnaryExpression.Op.Minus) ) {
                info.maxProblem = 4;
            }
        }

        return info;
    }

    /*
    func cutoff(e *ast.BinaryExpr, depth int) int {
	has4, has5, maxProblem := walkBinary(e)
	if maxProblem > 0 {
		return maxProblem + 1
	}
	if has4 && has5 {
		if depth == 1 {
			return 5
		}
		return 4
	}
	if depth == 1 {
		return 6
	}
	return 4
}

     */

    //    private Spacing spacing;
//
//    private static final TokenSet EMPTY_SET = TokenSet.create(
//            oMINUS, oPLUS, oMUL, oQUOTIENT, oREMAINDER,
//            oBIT_AND, oBIT_CLEAR, oBIT_XOR,
//            oSHIFT_LEFT, oSHIFT_RIGHT
//    );
//
//    public ExpressionBinary(ASTNode node, Alignment alignment, Wrap wrap, CommonCodeStyleSettings settings) {
//        super(node, alignment, Indent.getNormalIndent(), wrap, settings);
//
//        GoBinaryExpression psi = node.getPsi(GoBinaryExpression.class);
//
//        if (psi == null) {
//            spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
//            return;
//        }
//
//        ASTNode parentElement = node.getTreeParent();
//        ASTNode preParentElement = node;
//        IElementType parentElementType = parentElement.getElementType();
//
//        boolean inARelation = false;
//        if (parentElementType == REL_EXPRESSION) {
//            inARelation = true;
//        }
//
//        while (parentElementType != BUILTIN_CALL_EXPRESSION
//                && parentElementType != EXPRESSION_LIST
//                && !STMTS.contains(parentElementType)
//                && parentElementType != CONST_DECLARATION
//                && parentElementType != VAR_DECLARATION
//                ) {
//            preParentElement = parentElement;
//            parentElement = parentElement.getTreeParent();
//
//            if (parentElement == null) {
//                return;
//            }
//
//            parentElementType = parentElement.getElementType();
//
//            if (parentElementType == REL_EXPRESSION) {
//                inARelation = true;
//            }
//        }
//
//        if (inARelation) {
//            spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
//            return;
//        }
//
//        if (parentElementType == EXPRESSION_LIST) {
//            if (inTheSameLine(psi.getLeftOperand().getNode(), psi.getRightOperand().getNode())
//                && !(node.getElementType() == LOG_OR_EXPRESSION || node.getElementType() == LOG_AND_EXPRESSION || node.getElementType() == REL_EXPRESSION)
//                ) {
//                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
//            } else {
//                spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
//            }
//            return;
//        }
//
//        if (STMTS.contains(parentElementType)
//                || parentElementType == CONST_DECLARATION
//                || parentElementType == VAR_DECLARATION
//            ) {
//            try {
//                if (preParentElement.getElementType() == ADD_EXPRESSION
//                        && node != preParentElement
//                        && preParentElement.getPsi(GoAdditiveExpression.class).getOperator() == oBIT_OR) {
//                    spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
//                    return;
//                }
//            } catch (NullPointerException ignored) {
//
//            }
//
//            if (preParentElement.getElementType() == SLICE_EXPRESSION) {
//                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
//                return;
//            }
//
//            spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
//            return;
//        }
//
//        ASTNode expression = node;
//        ASTNode expressionChild = node;
//
//        while (expression.getElementType() != BUILTIN_CALL_EXPRESSION
//                ) {
//            expressionChild = expression;
//            expression = expression.getTreeParent();
//        }
//
//        if (expression.getElementType() != BUILTIN_CALL_EXPRESSION) {
//            spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
//            return;
//        }
//
//        ASTNode elem = expressionChild;
//        boolean isAlone = true;
//        while (!elem.getElementType().toString().equals(")")) {
//            elem = elem.getTreeNext();
//            if (elem.getElementType().toString().equals(",")) {
//                isAlone = false;
//            }
//        }
//
//        if (!isAlone) {
//            if (inTheSameLine(psi.getLeftOperand().getNode(), psi.getRightOperand().getNode())) {
//                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
//            } else {
//                spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
//            }
//            return;
//        }
//
//        elem = expressionChild;
//        while (!elem.getElementType().toString().equals("(")) {
//            elem = elem.getTreePrev();
//            if (elem.getElementType().toString().equals(",")) {
//                isAlone = false;
//            }
//        }
//
//        if (!isAlone) {
//            if (inTheSameLine(node, elem)) {
//                spacing = EMPTY_SPACING_KEEP_LINE_BREAKS;
//            } else {
//                spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
//            }
//            return;
//        }
//
//        spacing = BASIC_SPACING_KEEP_LINE_BREAKS;
//    }
//
//    @Override
//    protected Spacing getGoBlockSpacing(GoBlock child1, GoBlock child2) {
//        return isCommentBlock(child1) || isCommentBlock(child2) ? BASIC_SPACING_KEEP_LINE_BREAKS : spacing;
//    }
//
//    @Override
//    protected Indent getChildIndent(@Nullable PsiElement child) {
//        return null;
//    }
}
