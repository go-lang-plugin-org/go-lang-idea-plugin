package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

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
        switch (getUnaryOp()) {
            case Channel:
                if (basic.length == 1 && basic[0] instanceof GoTypeChannel) {
                    GoTypeChannel channelType = (GoTypeChannel) basic[0];
                    return new GoType[]{
                            channelType.getElementType(),
                            GoTypes.getBuiltin(GoTypes.Builtin.Bool,
                                    GoNamesCache.getInstance(getProject()))
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
            default:
                return basic;
        }
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
}
