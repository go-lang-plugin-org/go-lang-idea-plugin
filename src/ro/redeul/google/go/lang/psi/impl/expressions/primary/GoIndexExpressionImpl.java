package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIndexExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeArray;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeMap;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeArray;
import ro.redeul.google.go.lang.psi.typing.GoTypeMap;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoIndexExpressionImpl extends GoExpressionBase
    implements GoIndexExpression {
    public GoIndexExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        GoType[] baseTypes = getBaseExpression().getType();

        if (baseTypes.length != 1)
            return GoType.EMPTY_ARRAY;

        GoType baseType = baseTypes[0];
        GoUnderlyingType underlyingType = baseType.getUnderlyingType();

        if (underlyingType instanceof GoUnderlyingTypeSlice) {
            GoTypeSlice slice =
                GoTypes.resolveTo(baseType, GoTypeSlice.class);

            if (slice == null)
                return GoType.EMPTY_ARRAY;

            return new GoType[]{slice.getElementType()};
        }

        if (underlyingType instanceof GoUnderlyingTypeArray) {
            GoTypeArray typeArray =
                GoTypes.resolveTo(baseType, GoTypeArray.class);

            if (typeArray == null)
                return GoType.EMPTY_ARRAY;

            return new GoType[]{typeArray.getElementType()};
        }

        if (underlyingType instanceof GoUnderlyingTypeMap) {
            GoTypeMap map =
                GoTypes.resolveTo(baseType, GoTypeMap.class);

            if (map == null) {
                return GoType.EMPTY_ARRAY;
            }

            return new GoType[]{map.getElementType()};
        }

        // TODO: implement the case when the base has type string.

        return GoType.EMPTY_ARRAY;
    }

    @Override
    public GoExpr getIndex() {
        return findChildByClass(GoExpr.class, 1);
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class, 0);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitIndexExpression(this);
    }

    @Override
    public boolean isConstantExpression() {
        return false;
    }
}
