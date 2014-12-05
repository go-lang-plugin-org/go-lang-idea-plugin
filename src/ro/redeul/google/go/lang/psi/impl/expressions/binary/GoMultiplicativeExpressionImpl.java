package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoMultiplicativeExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoMultiplicativeExpression.Op;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.GoNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static ro.redeul.google.go.lang.psi.typing.GoTypeConstant.Kind.*;

public class GoMultiplicativeExpressionImpl extends GoBinaryExpressionImpl<Op> implements GoMultiplicativeExpression {

    public GoMultiplicativeExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Op op() {
        IElementType opTok = getOperator(GoElementTypes.MUL_OPS);

        if (opTok == GoElementTypes.oMUL) return Op.Mul;
        if (opTok == GoElementTypes.oQUOTIENT) return Op.Quotient;
        if (opTok == GoElementTypes.oREMAINDER) return Op.Remainder;
        if (opTok == GoElementTypes.oSHIFT_LEFT) return Op.ShiftLeft;
        if (opTok == GoElementTypes.oSHIFT_RIGHT) return Op.ShiftRight;
        if (opTok == GoElementTypes.oBIT_AND) return Op.BitAnd;
        if (opTok == GoElementTypes.oBIT_CLEAR) return Op.BitClear;

        return Op.None;
    }

    @Override
    protected GoType computeConstant(@NotNull GoTypeConstant left, @NotNull GoTypeConstant right) {
        if ( left.kind() == Boolean || right.kind() == Boolean)
            return GoType.Unknown;

        if ( left.kind() == String || right.kind() == String)
            return GoType.Unknown;

        if ( left.kind() == Complex || right.kind() == Complex ) {
            GoNumber leftValue = GoNumber.buildFrom(left.getValue());
            GoNumber rightValue = GoNumber.buildFrom(right.getValue());

            if ( leftValue == null || rightValue == null )
                return GoType.Unknown;

            switch (op()) {
                case Mul:
                    return GoTypes.constant(Complex, leftValue.multiply(rightValue));
                case Quotient:
                    if ( rightValue.equals(GoNumber.ZERO) )
                        return GoType.Unknown;

                    return GoTypes.constant(Complex, leftValue.divide(rightValue));
                default:
                    return GoType.Unknown;
            }
        }

        if ( left.kind() == Float || right.kind() == Float ) {
            BigDecimal leftValue = left.getValueAs(BigDecimal.class);
            BigDecimal rightValue = right.getValueAs(BigDecimal.class);

            if ( leftValue == null || rightValue == null )
                return GoType.Unknown;

            switch (op()){
                case Mul:
                    return GoTypes.constant(Float, leftValue.multiply(rightValue));
                case Quotient:
                    if ( rightValue.compareTo(BigDecimal.ZERO) == 0 )
                        return GoType.Unknown;

                    return GoTypes.constant(Float, leftValue.divide(rightValue, MathContext.DECIMAL128));
                case Remainder:
                    return GoTypes.constant(Float, leftValue.divideAndRemainder(rightValue)[1]);
                case ShiftLeft:
                    try {
                        BigInteger leftInteger = leftValue.toBigIntegerExact();
                        BigInteger rightInteger = rightValue.toBigIntegerExact();

                        return GoTypes.constant(Integer, leftInteger.shiftLeft(rightInteger.intValue()));
                    } catch (ArithmeticException ex) {
                        return GoType.Unknown;
                    }
                case ShiftRight:
                    try {
                        BigInteger leftInteger = leftValue.toBigIntegerExact();
                        BigInteger rightInteger = rightValue.toBigIntegerExact();

                        return GoTypes.constant(Integer, leftInteger.shiftRight(rightInteger.intValue()));
                    } catch (ArithmeticException ex) {
                        return GoType.Unknown;
                    }
                default:
                    return GoType.Unknown;
            }
        }

        if ( left.kind() == Integer || right.kind() == Integer ) {
            BigInteger leftValue = left.getValueAs(BigInteger.class);
            BigInteger rightValue = right.getValueAs(BigInteger.class);

            if ( leftValue == null || rightValue == null )
                return GoType.Unknown;

            switch (op()){
                case Mul:
                    return GoTypes.constant(Integer, leftValue.multiply(rightValue));
                case Quotient:
                    if ( rightValue.compareTo(BigInteger.ZERO) == 0 )
                        return GoType.Unknown;

                    return GoTypes.constant(Integer, leftValue.divide(rightValue));
                case Remainder:
                    if ( rightValue.compareTo(BigInteger.ZERO) == 0 )
                        return GoType.Unknown;

                    return GoTypes.constant(Integer, leftValue.divideAndRemainder(rightValue)[1]);
                case BitAnd:
                    return GoTypes.constant(Integer, leftValue.and(rightValue));
                case BitClear:
                    return GoTypes.constant(Integer, leftValue.andNot(rightValue));
                case ShiftLeft:
                    return GoTypes.constant(Integer, leftValue.shiftLeft(rightValue.intValue()));
                case ShiftRight:
                    return GoTypes.constant(Integer, leftValue.shiftRight(rightValue.intValue()));
                default:
                    return GoType.Unknown;
            }
        }

        return GoType.Unknown;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitMultiplicativeExpression(this);
    }
}
