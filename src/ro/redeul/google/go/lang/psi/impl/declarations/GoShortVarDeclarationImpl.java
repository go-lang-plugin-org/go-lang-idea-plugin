package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpression;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:28 PM
 */
public class GoShortVarDeclarationImpl extends GoPsiElementBase implements GoShortVarDeclaration {

    public GoShortVarDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoIdentifier[] getIdentifiers() {
        return findChildrenByClass(GoIdentifier.class);
    }

    @Override
    public GoExpression[] getExpressions() {
        return findChildrenByClass(GoExpression.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return processor.execute(this, state);
    }
}
