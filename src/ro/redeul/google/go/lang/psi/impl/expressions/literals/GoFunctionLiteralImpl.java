package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoFunctionLiteral;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfType;

public class GoFunctionLiteralImpl extends GoExpressionBase
    implements GoFunctionLiteral {

    public GoFunctionLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType resolveType() {
        return null;
    }

    @Override
    public GoFunctionParameter[] getParameters() {
        PsiElement functionType = findChildByType(GoElementTypes.TYPE_FUNCTION);

        return GoPsiUtils.getParameters(functionType);
    }

    @Override
    public GoFunctionParameter[] getResults() {
        PsiElement typeFunction = findChildOfType(this, GoElementTypes.TYPE_FUNCTION);
        PsiElement result = findChildOfType(typeFunction, GoElementTypes.FUNCTION_RESULT);

        return GoPsiUtils.getParameters(result);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionLiteral(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        for (GoFunctionParameter functionParameter : getParameters()) {
            if ( ! processor.execute(functionParameter, state) )  {
                return false;
            }
        }

        return processor.execute(this, state);
    }
}
