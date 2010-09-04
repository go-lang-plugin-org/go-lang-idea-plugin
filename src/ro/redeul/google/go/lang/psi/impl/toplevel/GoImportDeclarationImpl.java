package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 11:29:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoImportDeclarationImpl extends GoPsiElementImpl implements GoImportDeclaration {

    public GoImportDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoImportSpec[] getImports() {
        return findChildrenByClass(GoImportSpec.class); 
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitImportDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        GoImportSpec[] importSpecifications = getImports();

        for (GoImportSpec importSpecification : importSpecifications) {
            if ( ! importSpecification.processDeclarations(processor, state, lastParent, place) ) {
                return false;
            }
        }

        return true;
    }
}
