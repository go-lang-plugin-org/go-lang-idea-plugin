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
        GoExpr childByClass = findChildByClass(GoExpr.class);
        // TODO: Why is this here ? It shouldn't be
        if (childByClass instanceof GoParenthesisedExpression)
            return ((GoParenthesisedExpression) childByClass).getInnerExpression();
        return childByClass;
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
