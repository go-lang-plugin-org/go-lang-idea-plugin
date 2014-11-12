package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalAndExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

public class GoLogicalAndExpressionImpl extends GoBinaryExpressionImpl<GoLogicalAndExpression.Op>
    implements GoLogicalAndExpression
{
    public GoLogicalAndExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        return new GoType[]{
                types().getBuiltin(GoTypes.Builtin.Bool)
        };
    }

    @Override
    public Op op() {
        IElementType opTok = getOperator(GoElementTypes.LOG_AND_OPS);

        if (opTok == GoElementTypes.oCOND_AND) return Op.LogicalAnd;

        return Op.None;
    }

    @Override
    protected GoType computeConstant(GoTypeConstant left, GoTypeConstant right) {
        return GoType.Unknown;
    }
}

