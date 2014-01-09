package ro.redeul.google.go.util.expression;

import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;

import java.util.HashMap;
import java.util.Map;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.oCOND_AND;

public class FlipBooleanExpression {
    private static final Map<IElementType, IElementType> BINARY_FLIP = new HashMap<IElementType, IElementType>();

    private static void addBinaryFlip(IElementType type1, IElementType type2) {
        BINARY_FLIP.put(type1, type2);
        BINARY_FLIP.put(type2, type1);
    }

    static {
        addBinaryFlip(GoElementTypes.oLESS, GoElementTypes.oGREATER_OR_EQUAL);
        addBinaryFlip(GoElementTypes.oLESS_OR_EQUAL, GoElementTypes.oGREATER);
        addBinaryFlip(GoElementTypes.oEQ, GoElementTypes.oNOT_EQ);
    }

    public static String flip(GoExpr expr) {
        if (expr instanceof GoParenthesisedExpression) {
            return String.format("(%s)", flip(((GoParenthesisedExpression) expr).getInnerExpression()));
        }

        if (expr instanceof GoUnaryExpression) {
            GoUnaryExpression ue = (GoUnaryExpression) expr;
            if (ue.getOp() == GoUnaryExpression.Op.Not) {
                return ue.getExpression().getText();
            }
        }

        if (expr instanceof GoBinaryExpression) {
            GoBinaryExpression be = (GoBinaryExpression) expr;
            IElementType operator = be.getOperator();
            IElementType newOperator = BINARY_FLIP.get(operator);
            if (newOperator != null) {
                return flipBinary(be, newOperator);
            }

            if (operator == GoElementTypes.oCOND_OR) {
                String lhs = flipAndAddParenthesesIfItsAndExpr(be.getLeftOperand());
                String rhs = flipAndAddParenthesesIfItsAndExpr(be.getRightOperand());
                return lhs + " && " + rhs;
            } else if (operator == GoElementTypes.oCOND_AND) {
                return flip(be.getLeftOperand()) + " || " + flip(be.getRightOperand());
            }
        }

        if (expr instanceof GoLiteralExpression) {
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoLiteralIdentifier) {
                return "!" + expr.getText();
            }
        }

        if (expr instanceof GoCallOrConvExpression) {
            return "!" + expr.getText();
        }

        return String.format("!(%s)", expr.getText());
    }

    private static String flipAndAddParenthesesIfItsAndExpr(GoExpr expr) {
        if (expr instanceof GoBinaryExpression &&
            ((GoBinaryExpression) expr).getOperator() == oCOND_AND) {
            return String.format("(%s)", flip(expr));
        }
        return flip(expr);
    }

    private static String flipBinary(GoBinaryExpression expr, IElementType newOperator) {
        String lhs = expr.getLeftOperand().getText();
        String op = newOperator.toString();
        String rhs = expr.getRightOperand().getText();
        return lhs + " " + op + " " + rhs;
    }
}
