package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression.Op;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant.Kind;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.GoNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

import static ro.redeul.google.go.lang.psi.typing.GoTypeConstant.Kind.*;

public class GoAdditiveExpressionImpl extends GoBinaryExpressionImpl<Op> implements GoAdditiveExpression {
    public GoAdditiveExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Op op() {
        IElementType opTok = getOperator(GoElementTypes.ADD_OPS);

        if (opTok == GoElementTypes.oPLUS) return Op.Plus;
        if (opTok == GoElementTypes.oMINUS) return Op.Minus;
        if (opTok == GoElementTypes.oBIT_OR) return Op.BitOr;
        if (opTok == GoElementTypes.oBIT_XOR) return Op.BitXor;

        return Op.None;
    }

    @Override
    protected GoType computeConstant(@NotNull GoTypeConstant left, @NotNull GoTypeConstant right) {

        if ( left.kind() == Boolean || right.kind() == Boolean)
            return GoType.Unknown;

        if ( left.kind() == String ) {
            if ( right.kind() != String || op() != Op.Plus)
                return GoType.Unknown;

            String lString = left.getValueAs(String.class);
            String rString = right.getValueAs(String.class);

            if ( lString == null || rString == null )
                return GoType.Unknown;


            return GoTypes.constant(String, lString.concat(rString));
        }

        if ( left.kind() == Complex || right.kind() == Complex ) {
            GoNumber leftValue = GoNumber.buildFrom(left.getValue());
            GoNumber rightValue = GoNumber.buildFrom(right.getValue());

            if ( leftValue == null || rightValue == null )
                return GoType.Unknown;

            switch (op()) {
                case Plus:
                    return GoTypes.constant(Complex, leftValue.add(rightValue));
                case Minus:
                    return GoTypes.constant(Complex, leftValue.substract(rightValue));
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
                case Plus:
                    return GoTypes.constant(Float, leftValue.add(rightValue));
                case Minus:
                    return GoTypes.constant(Float, leftValue.subtract(rightValue));
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
                case Plus:
                    return GoTypes.constant(Integer, leftValue.add(rightValue));
                case Minus:
                    return GoTypes.constant(Integer, leftValue.subtract(rightValue));
                case BitXor:
                    return GoTypes.constant(Integer, leftValue.xor(rightValue));
                case BitOr:
                    return GoTypes.constant(Integer, leftValue.or(rightValue));
                default:
                    return GoType.Unknown;
            }
        }

        return GoType.Unknown;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitAdditiveExpression(this);
    }
}

