package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 11:29:41 PM
 */
public class GoImportDeclarationsImpl extends GoPsiElementBase implements GoImportDeclarations {

    public GoImportDeclarationsImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoImportDeclaration[] getDeclarations() {
        return findChildrenByClass(GoImportDeclaration.class);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitImportDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        GoImportDeclaration[] importSpecifications = getDeclarations();

        for (GoImportDeclaration importSpecification : importSpecifications) {
            if (importSpecification != lastParent) {
                if ( ! importSpecification.processDeclarations(processor, state, null, place)) {
                    return false;
                }
            }
        }

        return true;
    }
}
