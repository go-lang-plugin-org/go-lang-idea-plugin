package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoTypeAssertionExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

public class GoTypeAssertionExpressionImpl extends GoExpressionBase implements GoTypeAssertionExpression {

    public GoTypeAssertionExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        return new GoType[]{
            types().fromPsiType(getAssertedType()),
            // this should be in here once we have the concept of a single-value context for evaluating an expression
//            types().getBuiltin(GoTypes.Builtin.Bool)
        };
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    public GoPsiType getAssertedType() {
        return findChildByClass(GoPsiType.class);
    }
}
