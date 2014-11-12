package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoRelationalExpressionImpl extends GoBinaryExpressionImpl<GoRelationalExpression.Op>
    implements GoRelationalExpression
{
    public GoRelationalExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        return new GoType[]{types().getBuiltin(GoTypes.Builtin.Bool)};
    }


    @Override
    @NotNull
    public Op op() {
        IElementType opTok = getOperator(GoElementTypes.REL_OPS);

        if (opTok == GoElementTypes.oEQ) return Op.Eq;
        if (opTok == GoElementTypes.oNOT_EQ) return Op.NotEq;
        if (opTok == GoElementTypes.oLESS) return Op.Less;
        if (opTok == GoElementTypes.oLESS_OR_EQUAL) return Op.LessOrEqual;
        if (opTok == GoElementTypes.oGREATER) return Op.Greater;
        if (opTok == GoElementTypes.oGREATER_OR_EQUAL) return Op.GreaterOrEqual;

        return Op.None;
    }

    @Override
    protected GoType computeConstant(GoTypeConstant left, GoTypeConstant right) {
        return GoType.Unknown;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitRelExpression(this);
    }
}

