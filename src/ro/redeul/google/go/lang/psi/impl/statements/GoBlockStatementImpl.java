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

import java.util.List;

public class GoBlockStatementImpl extends GoPsiElementBase implements GoBlockStatement {
    public GoBlockStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoStatement[] getStatements() {
        return findChildrenByClass(GoStatement.class);
    }

    static TokenSet tokenSet = TokenSet.create( GoElementTypes.SHORT_VAR_STATEMENT, GoElementTypes.CONST_DECLARATIONS,  GoElementTypes.VAR_DECLARATIONS, GoElementTypes.TYPE_DECLARATIONS);

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        PsiElement[] children = getChildren();

        boolean before = false;
        for (int i = children.length - 1; i >= 0; i--) {
            if (children[i] == lastParent) {
                before = true;
                continue;
            }

            if ( before && tokenSet.contains(children[i].getNode().getElementType()) ) {
                if ( ! children[i].processDeclarations(processor, state, null, place) ) {
                    return false;
                }
            }
        }

        return true;
    }
}
