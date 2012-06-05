package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/16/11
 * Time: 4:09 AM
 */
public class GoConstDeclarationsImpl extends GoPsiElementBase implements GoConstDeclarations {

    public GoConstDeclarationsImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoConstDeclaration[] getDeclarations() {
        return findChildrenByClass(GoConstDeclaration.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitConstDeclarations(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        PsiElement node = lastParent != null ? lastParent.getPrevSibling() : this.getLastChild();

        while ( node != null ) {

            if ( GoPsiUtils.isNodeOfType(node, GoElementTypes.CONST_DECLARATION) ) {
                if ( ! processor.execute(node, state) ) {
                    return false;
                }
            }

            node = node.getPrevSibling();
        }

        return true;
    }
}
