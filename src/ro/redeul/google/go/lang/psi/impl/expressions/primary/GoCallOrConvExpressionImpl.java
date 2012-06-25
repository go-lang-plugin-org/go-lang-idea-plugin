package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import java.util.Arrays;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoCallOrConvExpressionImpl extends GoPsiElementBase
    implements GoCallOrConvExpression
{
    public GoCallOrConvExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoType getType() {
        return null;
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    public GoType getTypeArgument() {
        return findChildByClass(GoType.class);
    }

    @Override
    public GoExpr[] getArguments() {

        GoExpr []expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length <= 1) {
            return GoExpr.EMPTY_ARRAY;
        }

        return Arrays.copyOfRange(expressions, 1, expressions.length);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitCallOrConvExpression(this);
    }
}
