package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoTypeAssertionExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypes;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

public class GoTypeAssertionExpressionImpl extends GoExpressionBase
    implements GoTypeAssertionExpression {
    public GoTypeAssertionExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());
        return new GoType[]{
            getAssertedType(),
            GoTypes.getBuiltin(GoTypes.Builtin.Bool, namesCache)
        };
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    public GoType getAssertedType() {
        return findChildByClass(GoType.class);
    }
}
