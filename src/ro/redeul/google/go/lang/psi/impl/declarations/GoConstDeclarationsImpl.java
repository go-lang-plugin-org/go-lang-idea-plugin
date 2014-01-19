package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.declarations.GoConstSpec;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.impl.GoDocumentedPsiElementBase;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/16/11
 * Time: 4:09 AM
 */
public class GoConstDeclarationsImpl extends GoDocumentedPsiElementBase implements GoConstDeclarations {

    public GoConstDeclarationsImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoConstSpec[] getDeclarations() {
        return findChildrenByClass(GoConstSpec.class);
    }

    @Override
    public boolean isMulti() {
        return findChildByType(GoTokenTypes.pLPAREN) != null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitConstDeclarations(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitConstDeclaration(this, data);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        PsiElement child = getLastChild();

        while (child != null) {
            if (child != lastParent &&
                !child.processDeclarations(processor, state, null, place))
                return false;

            child = child.getPrevSibling();
        }

        return true;
    }
}
