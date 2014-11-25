package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalAndExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.typing.GoTypeConstant.Kind.Boolean;

public class GoLogicalAndExpressionImpl extends GoBinaryExpressionImpl<GoLogicalAndExpression.Op> implements GoLogicalAndExpression
{
    public GoLogicalAndExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        return super.resolveTypes();
    }

    @Override
    public Op op() {
        IElementType opTok = getOperator(GoElementTypes.LOG_AND_OPS);

        if (opTok == GoElementTypes.oCOND_AND) return Op.LogicalAnd;

        return Op.None;
    }

    @Override
    protected GoType computeConstant(@NotNull GoTypeConstant left, @NotNull GoTypeConstant right) {
        GoType builtinBool = GoTypes.getInstance(getProject()).getBuiltin(GoTypes.Builtin.Bool);

        if ( left.kind() != Boolean || right.kind() != Boolean)
            return builtinBool;

        Boolean leftValue = left.getValueAs(Boolean.class);
        Boolean rightValue = left.getValueAs(Boolean.class);

        boolean value = false;
        if ( leftValue != null && rightValue != null )
            value = leftValue && rightValue;

        return GoTypes.constant(Boolean, value, builtinBool);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLogicalAndExpression(this);
    }
}

