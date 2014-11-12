package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression.Op;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.util.GoNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GoAdditiveExpressionImpl extends GoBinaryExpressionImpl<Op> implements GoAdditiveExpression {
    public GoAdditiveExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Op op() {
        IElementType opTok = getOperator(GoElementTypes.ADD_OPS);

        if (opTok == GoElementTypes.oPLUS) return Op.Plus;
        if (opTok == GoElementTypes.oMINUS) return Op.Minus;
        if (opTok == GoElementTypes.oBIT_AND) return Op.BitAnd;
        if (opTok == GoElementTypes.oBIT_OR) return Op.BitOr;

        return Op.None;
    }

    @Override
    protected GoType computeConstant(GoTypeConstant left, GoTypeConstant right) {

        GoTypes types = GoTypes.getInstance(getProject());

        if ( left.getKind() == GoTypeConstant.Kind.Boolean || right.getKind() == GoTypeConstant.Kind.Boolean)
            return GoType.Unknown;

        if ( left.getKind() == GoTypeConstant.Kind.String ) {
            if ( right.getKind() != GoTypeConstant.Kind.String || op() != Op.Plus)
                return GoType.Unknown;

            String lString = left.getValueAs(String.class);
            String rString = right.getValueAs(String.class);

            if ( lString == null || rString == null )
                return GoType.Unknown;


            return GoTypes.constant(GoTypeConstant.Kind.String, lString.concat(rString));
        }

        if ( left.getKind() == GoTypeConstant.Kind.Complex || right.getKind() == GoTypeConstant.Kind.Complex ) {
            GoNumber leftValue = GoNumber.buildFrom(left.getValue());
            GoNumber rightValue = GoNumber.buildFrom(right.getValue());

            if ( leftValue == null || rightValue == null )
                return GoType.Unknown;

            switch (op()) {
                case Plus:
                    return GoTypes.constant(GoTypeConstant.Kind.Complex, leftValue.add(rightValue));
                case Minus:
                    return GoTypes.constant(GoTypeConstant.Kind.Complex, leftValue.substract(rightValue));
                default:
                    return GoType.Unknown;
            }
        }

        if ( left.getKind() == GoTypeConstant.Kind.Float || right.getKind() == GoTypeConstant.Kind.Float ) {
            BigDecimal leftValue = left.getValueAs(BigDecimal.class);
            BigDecimal rightValue = right.getValueAs(BigDecimal.class);

            if ( leftValue == null || rightValue == null )
                return GoType.Unknown;

            switch (op()){
                case Plus:
                    return GoTypes.constant(GoTypeConstant.Kind.Float, leftValue.add(rightValue));
                case Minus:
                    return GoTypes.constant(GoTypeConstant.Kind.Float, leftValue.subtract(rightValue));
                default:
                    return GoType.Unknown;
            }
        }

        if ( left.getKind() == GoTypeConstant.Kind.Integer || right.getKind() == GoTypeConstant.Kind.Integer ) {
            BigInteger leftValue = left.getValueAs(BigInteger.class);
            BigInteger rightValue = right.getValueAs(BigInteger.class);

            if ( leftValue == null || rightValue == null )
                return GoType.Unknown;

            switch (op()){
                case Plus:
                    return GoTypes.constant(GoTypeConstant.Kind.Integer, leftValue.add(rightValue));
                case Minus:
                    return GoTypes.constant(GoTypeConstant.Kind.Integer, leftValue.subtract(rightValue));
                case BitAnd:
                    return GoTypes.constant(GoTypeConstant.Kind.Integer, leftValue.and(rightValue));
                case BitOr:
                    return GoTypes.constant(GoTypeConstant.Kind.Integer, leftValue.or(rightValue));
                default:
                    return GoType.Unknown;
            }
        }

        return GoType.Unknown;
    }
}

