package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

public class GoUnaryExpressionImpl extends GoExpressionBase implements GoUnaryExpression {

    public GoUnaryExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        GoExpr expression = getExpression();
        if (expression == null)
            return GoType.EMPTY_ARRAY;
        GoType[] basic = expression.getType();
        switch (getOp()) {
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
    public Op getOp() {
        return GoPsiUtils.findOperator(this, Op.values(), GoTokenSets.OPS_UNARY);
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
