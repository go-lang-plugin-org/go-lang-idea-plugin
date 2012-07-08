package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import java.util.Arrays;
import java.util.List;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoCallOrConvExpressionImpl extends GoExpressionBase
    implements GoCallOrConvExpression
{
    public GoCallOrConvExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
  //      GoType []types = getBaseExpression().getType();

        // TODO: implement resolving of types that are calls.
        return super.resolveTypes();
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    public GoType getTypeArgument() {
        return findChildByClass(GoType.class);
    }

    @Override
    public GoExpr[] getArguments() {

        PsiElement list = findChildByType(GoElementTypes.EXPRESSION_LIST);
        if ( list != null ) {
            List<GoExpr> arguments =
                GoPsiUtils.findChildrenOfType(list, GoExpr.class);
            return arguments.toArray(new GoExpr[arguments.size()]);
        }

        GoExpr []expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length <= 1) {
            return GoExpr.EMPTY_ARRAY;
        }

        return Arrays.copyOfRange(expressions, 1, expressions.length);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitCallOrConvExpression(this);
    }
}
