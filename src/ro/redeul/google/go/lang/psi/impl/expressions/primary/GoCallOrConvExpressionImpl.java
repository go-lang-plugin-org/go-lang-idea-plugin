package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.Arrays;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class GoCallOrConvExpressionImpl extends GoExpressionBase
    implements GoCallOrConvExpression
{
    public GoCallOrConvExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        PsiElement reference = resolveSafely(getBaseExpression(), PsiElement.class);
        if (reference == null)
            return GoType.EMPTY_ARRAY;

        if (reference.getParent() instanceof GoMethodDeclaration ) {
            GoMethodDeclaration declaration = (GoMethodDeclaration)reference.getParent();
            return GoTypes.fromPsiType(declaration.getReturnType());
        }

        if (reference.getParent() instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration declaration = (GoFunctionDeclaration)reference.getParent();
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
        GoExpr[] expressions = findChildrenByClass(GoExpr.class);
        return Arrays.copyOfRange(expressions, 1, expressions.length);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitCallOrConvExpression(this);
    }

    @Override
    public boolean isConstantExpression() {
        PsiElement reference = resolveSafely(getBaseExpression(), PsiElement.class);

        if (reference instanceof GoTypeSpec) {
            GoExpr[] arguments = getArguments();
            return arguments.length == 1 && arguments[0].isConstantExpression();
        }

        return false;
    }
}
