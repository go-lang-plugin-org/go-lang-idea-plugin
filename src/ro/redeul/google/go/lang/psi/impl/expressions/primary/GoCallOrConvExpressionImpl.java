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
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class GoCallOrConvExpressionImpl extends GoExpressionBase
    implements GoCallOrConvExpression
{
    public GoCallOrConvExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        PsiElement myself = resolveSafely(getBaseExpression(), PsiElement.class);
        if (myself == null)
            return GoType.EMPTY_ARRAY;

        if (myself.getParent() instanceof GoMethodDeclaration )
        {
            GoMethodDeclaration declaration = (GoMethodDeclaration)myself.getParent();

            return GoTypes.fromPsiType(declaration.getReturnType());
        }

        return GoType.EMPTY_ARRAY;
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    public GoPsiType getTypeArgument() {
        return findChildByClass(GoPsiType.class);
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
