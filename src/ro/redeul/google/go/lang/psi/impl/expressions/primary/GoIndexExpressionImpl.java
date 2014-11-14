package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIndexExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoIndexExpressionImpl extends GoExpressionBase
    implements GoIndexExpression {
    public GoIndexExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        GoType[] baseTypes = getBaseExpression().getType();

        if (baseTypes.length != 1)
            return GoType.EMPTY_ARRAY;

        GoType baseType = baseTypes[0];
        GoType underlyingType = baseType.underlyingType();

        return underlyingType.accept(new TypeVisitor<GoType[]>(GoType.EMPTY_ARRAY) {
            @Override
            public GoType[] visitSlice(GoTypeSlice type) {
                return new GoType[] { type.getElementType() };
            }

            @Override
            public GoType[] visitArray(GoTypeArray type) {
                return new GoType[] { type.getElementType() };
            }

            @Override
            public GoType[] visitMap(GoTypeMap map) {
                return new GoType[] { map.getElementType() };
            }
        });
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
}
