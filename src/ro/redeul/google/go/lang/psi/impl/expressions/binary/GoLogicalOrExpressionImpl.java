package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalOrExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

public class GoLogicalOrExpressionImpl extends GoBinaryExpressionImpl<GoLogicalOrExpression.Op>
    implements GoLogicalOrExpression
{
    public GoLogicalOrExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        return super.resolveTypes();
    }

    @Override
    public Op op() {
        IElementType opTok = getOperator(GoElementTypes.LOG_OR_OPS);

        if (opTok == GoElementTypes.oCOND_OR) return Op.LogicalOr;

        return Op.None;
    }

    @Override
    protected GoType computeConstant(@NotNull GoTypeConstant left, @NotNull GoTypeConstant right) {

        GoType builtinBool = GoTypes.getInstance(getProject()).getBuiltin(GoTypes.Builtin.Bool);

        if ( left.getKind() != GoTypeConstant.Kind.Boolean || right.getKind() != GoTypeConstant.Kind.Boolean)
            return builtinBool;

        Boolean leftValue = left.getValueAs(Boolean.class);
        Boolean rightValue = left.getValueAs(Boolean.class);

        boolean value = false;
        if ( leftValue != null && rightValue != null )
            value = leftValue || rightValue;

        return GoTypes.constant(GoTypeConstant.Kind.Boolean, value, builtinBool);
    }
}

