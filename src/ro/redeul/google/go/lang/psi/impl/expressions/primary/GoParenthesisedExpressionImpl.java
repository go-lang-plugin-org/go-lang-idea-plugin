package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

public class GoParenthesisedExpressionImpl extends GoPsiElementBase
    implements GoParenthesisedExpression {
    public GoParenthesisedExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getInnerExpression() {
        return findChildByClass(GoExpr.class);
    }

    @NotNull
    @Override
    public GoType[] getType() {
        return getInnerExpression().getType();
    }

    @Override
    public boolean isConstantExpression() {
        return getInnerExpression().isConstantExpression();
    }

    @Override
    public boolean hasType(GoTypes.Builtin builtinType) {
        return getInnerExpression().hasType(builtinType);
    }

    @Override
    public boolean hasType(GoType type) {
        return getInnerExpression().hasType(type);
    }
}
