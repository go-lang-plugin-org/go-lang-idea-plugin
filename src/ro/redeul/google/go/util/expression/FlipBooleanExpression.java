package ro.redeul.google.go.util.expression;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalAndExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalOrExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;

public class FlipBooleanExpression {

    public static String flip(GoExpr expr) {
        if (expr instanceof GoParenthesisedExpression) {
            return String.format("(%s)", flip(((GoParenthesisedExpression) expr).getInnerExpression()));
        }

        if (expr instanceof GoUnaryExpression) {
            GoUnaryExpression ue = (GoUnaryExpression) expr;
            if (ue.getUnaryOp() == GoUnaryExpression.Op.Not) {
                return ue.getExpression().getText();
            }
        }

        if (expr instanceof GoRelationalExpression) {
            GoRelationalExpression relationalExpr = (GoRelationalExpression) expr;

            switch (relationalExpr.op()) {
                case Less:
                    return flipRelational(relationalExpr, GoRelationalExpression.Op.GreaterOrEqual);
                case LessOrEqual:
                    return flipRelational(relationalExpr, GoRelationalExpression.Op.Greater);
                case Eq:
                    return flipRelational(relationalExpr, GoRelationalExpression.Op.NotEq);
                case NotEq:
                    return flipRelational(relationalExpr, GoRelationalExpression.Op.Eq);
                case GreaterOrEqual:
                    return flipRelational(relationalExpr, GoRelationalExpression.Op.Less);
                case Greater:
                    return flipRelational(relationalExpr, GoRelationalExpression.Op.LessOrEqual);

            }
        }

        if ( expr instanceof GoLogicalOrExpression) {
            GoBinaryExpression be = (GoLogicalOrExpression) expr;

            String lhs = flipAndAddParenthesesIfItsAndExpr(be.getLeftOperand());
            String rhs = flipAndAddParenthesesIfItsAndExpr(be.getRightOperand());
            return lhs + " && " + rhs;
        }

        if ( expr instanceof GoLogicalAndExpression) {
            GoBinaryExpression be = (GoBinaryExpression) expr;

            return flip(be.getLeftOperand()) + " || " + flip(be.getRightOperand());
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
        if (expr instanceof GoLogicalAndExpression) {
            return String.format("(%s)", flip(expr));
        }

        return flip(expr);
    }

    private static String flipRelational(GoRelationalExpression expr, GoRelationalExpression.Op op) {
        String lhs = expr.getLeftOperand().getText();
        String rhs = expr.getRightOperand().getText();
        return lhs + " " + op.tokenText() + " " + rhs;
    }
}
