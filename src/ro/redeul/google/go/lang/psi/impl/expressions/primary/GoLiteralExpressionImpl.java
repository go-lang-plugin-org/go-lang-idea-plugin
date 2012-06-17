package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLiteralExpressionImpl extends GoPsiElementBase
    implements GoLiteralExpression {

    public GoLiteralExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralExpression(this);
    }

    @Override
    public GoLiteral getLiteral() {
        return findChildByClass(GoLiteral.class);
    }

    @Override
    public GoType getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //    @Override
//    public GoLiteralIdentifier getIdentifier() {
//        return findChildByClass(GoLiteralIdentifier.class);
//    }
//
//    @Override
    protected GoType resolveType() {

//        if ( this.getIdentifier() == null ) {
//            return null;
//        }

//        VariableTypeResolver variableTypeResolver = new VariableTypeResolver(this.getIdentifier());
//
//        if ( ! PsiScopesUtil.treeWalkUp(variableTypeResolver, this, this.getContainingFile(), GoResolveStates.variables()) ) {
//            return variableTypeResolver.getResolvedType();
//        }
//
        return null;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return
            PsiScopesUtil.walkChildrenScopes(this,
                                             processor, state,
                                             lastParent, place);
    }

    @Override
    public PsiReference getReference() {
        return null;
    }
}
