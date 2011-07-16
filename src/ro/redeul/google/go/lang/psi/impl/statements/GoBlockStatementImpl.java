package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;

import java.util.List;

public class GoBlockStatementImpl extends GoPsiElementBase implements GoBlockStatement {
    public GoBlockStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoStatement[] getStatements() {
        return findChildrenByClass(GoStatement.class);
    }

    static TokenSet tokenSet = TokenSet.create(
            GoElementTypes.SHORT_VAR_STATEMENT, GoElementTypes.CONST_DECLARATIONS,
            GoElementTypes.VAR_DECLARATIONS,  GoElementTypes.TYPE_DECLARATIONS);

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        PsiElement node = lastParent != null ? lastParent.getPrevSibling() : this.getLastChild();

        while ( node != null ) {

            if (GoPsiUtils.isNodeOfType(node, GoTokenSets.GO_BLOCK_ENTRY_POINT_TYPES)) {

                if ( ! node.processDeclarations(processor, state, null, place) ) {
                    return false;
                }
            }

            node = node.getPrevSibling();
        }

        return true;
    }
}
