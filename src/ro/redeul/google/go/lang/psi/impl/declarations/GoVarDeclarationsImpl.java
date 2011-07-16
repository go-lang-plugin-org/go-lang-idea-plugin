package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/16/11
 * Time: 3:22 AM
 */
public class GoVarDeclarationsImpl extends GoPsiElementBase implements GoVarDeclarations {

    public GoVarDeclarationsImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoVarDeclaration[] getDeclarations() {
        return findChildrenByClass(GoVarDeclaration.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        PsiElement node = lastParent != null ? lastParent.getPrevSibling() : this.getLastChild();

        while ( node != null ) {

            if ( GoPsiUtils.isNodeOfType(node, GoElementTypes.VAR_DECLARATION) ) {
                if ( ! processor.execute(node, state) ) {
                    return false;
                }
            }

            node = node.getPrevSibling();
        }

        return true;
    }

}
