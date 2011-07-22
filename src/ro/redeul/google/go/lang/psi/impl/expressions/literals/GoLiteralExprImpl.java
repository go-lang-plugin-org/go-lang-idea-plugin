package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.processors.VariableTypeResolver;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 11:08 PM
 */
public class GoLiteralExprImpl extends GoExpressionBase implements GoLiteralExpression {

    public GoLiteralExprImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralExpr(this);
    }

    @Override
    public GoIdentifier getIdentifier() {
        return findChildByClass(GoIdentifier.class);
    }

    @Override
    protected GoType resolveType() {

        if ( this.getIdentifier() == null ) {
            return null;
        }

        VariableTypeResolver variableTypeResolver = new VariableTypeResolver(this.getIdentifier());

        if ( ! PsiScopesUtil.treeWalkUp(variableTypeResolver, this, this.getContainingFile(), GoResolveStates.variables()) ) {
            return variableTypeResolver.getResolvedType();
        }

        return null;
    }

    public String toString() {
        return "LiteralExpr";
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return super.processDeclarations(processor, state, lastParent, place);
    }

    @Override
    public PsiReference getReference() {
        return null;
    }
}
