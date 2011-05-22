package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ImportedPackagesCollectorProcessor;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.lang.psi.types.GoType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 11:08 PM
 */
public class GoLiteralExpressionImpl extends GoPsiExpressionImpl implements GoLiteralExpression {

    public GoLiteralExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoIdentifier getIdentifier() {
        return findChildByClass(GoIdentifier.class);
    }

    @Override
    protected GoType resolveType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString() {
        return "LiteralExpr";
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return super.processDeclarations(processor, state, lastParent, place);
    }


}
