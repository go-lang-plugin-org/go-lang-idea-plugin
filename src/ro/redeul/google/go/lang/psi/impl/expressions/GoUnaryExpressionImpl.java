package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.GoNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

import static ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression.Op.Channel;

public class GoUnaryExpressionImpl extends GoExpressionBase
        implements GoUnaryExpression {
    public GoUnaryExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        GoExpr expression = getExpression();
        if (expression == null)
            return GoType.EMPTY_ARRAY;

        GoType[] basic = expression.getType();

        if ( basic.length == 1 && basic[0] instanceof GoTypeConstant ) {
            GoType myType = computeConstant((GoTypeConstant) basic[0]);
            return new GoType[]{myType};
        }

        switch (getUnaryOp()) {
            case Channel:
                if (basic.length == 1 && basic[0] instanceof GoTypeChannel) {
                    GoTypeChannel channelType = (GoTypeChannel) basic[0];
                    return new GoType[]{
                            channelType.getElementType(),
                            types().getBuiltin(GoTypes.Builtin.Bool)
                    };
                }
                return GoType.EMPTY_ARRAY;
            case Pointer:
                if (basic.length == 1 && basic[0] instanceof GoTypePointer) {
                    GoTypePointer pointerType = (GoTypePointer) basic[0];
                    return new GoType[]{
                            pointerType.getTargetType()
                    };
                }
                return GoType.EMPTY_ARRAY;
            case Address:
                if (basic.length == 1) {
                    return new GoType[]{
                            new GoTypePointer(basic[0])
                    };
                }
                return GoType.EMPTY_ARRAY;
            case Minus:

            default:
                return basic;
        }
    }

    private GoType computeConstant(GoTypeConstant constant) {

        switch (constant.getKind()) {
            case String:
            case Boolean:
                return GoType.Unknown;
            case Integer:
                BigInteger intValue = constant.getValueAs(BigInteger.class);
                if ( intValue == null )
                    return GoType.Unknown;

                switch (getUnaryOp()) {
                    case Plus:
                        return constant;
                    case Minus:
                        return GoTypes.constant(GoTypeConstant.Kind.Integer, intValue.negate());
                    case Not:
                        return GoTypes.constant(GoTypeConstant.Kind.Integer, intValue.not());
                    case Xor:
                        return GoTypes.constant(GoTypeConstant.Kind.Integer, intValue.not());
                    default:
                        return GoType.Unknown;
                }

            case Float:
                BigDecimal decimalValue = constant.getValueAs(BigDecimal.class);
                if ( decimalValue == null )
                    return GoType.Unknown;

                switch (getUnaryOp()) {
                    case Plus:
                        return constant;
                    case Minus:
                        return GoTypes.constant(GoTypeConstant.Kind.Float, decimalValue.negate());
                    default:
                        return GoType.Unknown;
                }

            case Complex:
                GoNumber complexValue = constant.getValueAs(GoNumber.class);
                if ( complexValue == null )
                    return GoType.Unknown;

                switch (getUnaryOp()) {
                    case Plus:
                        return constant;
                    case Minus:
                        return GoTypes.constant(GoTypeConstant.Kind.Complex, complexValue.negate());
                    default:
                        return GoType.Unknown;
                }
        }

        return GoType.Unknown;
    }

    @Override
    @NotNull
    public Op getUnaryOp() {
        PsiElement operatorChild = findChildByType(GoTokenSets.UNARY_OPS);
        if (operatorChild == null)
            return Op.None;

        IElementType elementType = operatorChild.getNode().getElementType();
        if (elementType == GoTokenTypes.oPLUS)
            return Op.Plus;

        if (elementType == GoTokenTypes.oMINUS)
            return Op.Minus;

        if (elementType == GoTokenTypes.oNOT)
            return Op.Not;

        if (elementType == GoTokenTypes.oBIT_XOR)
            return Op.Xor;

        if (elementType == GoTokenTypes.oMUL)
            return Op.Pointer;

        if (elementType == GoTokenTypes.oBIT_AND)
            return Op.Address;

        if (elementType == GoTokenTypes.oSEND_CHANNEL)
            return Channel;

        return Op.None;
    }

    @Override
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public boolean isConstantExpression() {
        GoExpr expression = getExpression();
        return expression != null && expression.isConstantExpression();
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitUnaryExpression(this);
    }
}
