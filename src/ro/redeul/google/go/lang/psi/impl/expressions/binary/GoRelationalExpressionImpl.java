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

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        GoType[] types = super.resolveTypes();
        if ( types.length == 1 && types[0] instanceof GoTypeConstant && ((GoTypeConstant)types[0]).getKind() == GoTypeConstant.Kind.Boolean)
            return types;

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
    protected GoType computeConstant(@NotNull GoTypeConstant left, @NotNull GoTypeConstant right) {
        // TODO: finish this implementation (actually calculate the values and set the constant here)
        return GoTypes.constant(GoTypeConstant.Kind.Boolean, false);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitRelExpression(this);
    }
}

