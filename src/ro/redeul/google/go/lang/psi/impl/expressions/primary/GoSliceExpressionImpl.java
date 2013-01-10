package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSliceExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.hasPrevSiblingOfType;

public class GoSliceExpressionImpl extends GoExpressionBase
    implements GoSliceExpression {

    public GoSliceExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSliceExpression(this);
    }

    @Override
    protected GoType[] resolveTypes() {
        return getBaseExpression().getType();
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class, 0);
    }

    @Override
    public GoExpr getFirstIndex() {
        GoExpr expr = findChildByClass(GoExpr.class, 1);
        if ( expr != null && hasPrevSiblingOfType(expr, GoTokenTypes.pLBRACK)) {
            return expr;
        }

        return null;
    }

    @Override
    public GoExpr getSecondIndex() {
        GoExpr expressions[] = findChildrenByClass(GoExpr.class);

        GoExpr expr = expressions[expressions.length - 1];
        if ( hasPrevSiblingOfType(expr, GoTokenTypes.oCOLON)) {
            return expr;
        }

        return null;
    }

    @Override
    public boolean isConstantExpression() {
        return false;
    }
}
