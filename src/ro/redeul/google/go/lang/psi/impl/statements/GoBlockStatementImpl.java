package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 3:15:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoBlockStatementImpl extends GoPsiElementImpl implements GoBlockStatement {
    public GoBlockStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoStatement[] getStatements() {
        return findChildrenByClass(GoStatement.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        GoTypeDeclaration[] children = findChildrenByClass(GoTypeDeclaration.class);

        for (GoTypeDeclaration child : children) {
            if ( ! child.processDeclarations(processor, state, null, place) ) {
                return false;
            }

            if (lastParent == child) {
                break;
            }
        }

        return super.processDeclarations(processor, state, lastParent, place);
    }
}
