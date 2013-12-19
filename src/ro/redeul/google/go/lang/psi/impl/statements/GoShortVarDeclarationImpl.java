package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.declarations.GoVarDeclarationImpl;
import ro.redeul.google.go.lang.psi.resolve.ShortVarDeclarationResolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:28 PM
 */
public class GoShortVarDeclarationImpl extends GoVarDeclarationImpl
    implements GoShortVarDeclaration {

    private List<GoLiteralIdentifier> declarations;

    public GoShortVarDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitShortVarDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if (lastParent != null)
            return true;
        return processor.execute(this, state);
    }

    @Override
    public GoLiteralIdentifier[] getDeclarations() {
        if (declarations == null){
            declarations = new ArrayList<GoLiteralIdentifier>();
            for (GoLiteralIdentifier identifier: getIdentifiers()) {
                if (ShortVarDeclarationResolver.resolve(identifier) == null ) {
                    declarations.add(identifier);
                }
            }
        }
        return declarations.toArray(new GoLiteralIdentifier[declarations.size()]);
    }
}
