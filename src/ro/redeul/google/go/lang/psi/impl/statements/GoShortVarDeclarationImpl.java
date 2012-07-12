package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.declarations.GoVarDeclarationImpl;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:28 PM
 */
public class GoShortVarDeclarationImpl extends GoVarDeclarationImpl
    implements GoShortVarDeclaration {

    public GoShortVarDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitShortVarDeclaration(this);
    }

    @Override
    public boolean mayRedeclareVariable() {
        return true;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                         @NotNull PsiElement place) {
        int index = 0;

        while (lastParent != null && lastParent instanceof GoExpr) {
            do {
                lastParent = lastParent.getPrevSibling();
            } while (lastParent != null &&
                    !(lastParent instanceof GoExpr) &&
                    lastParent.getNode().getElementType() != GoTokenTypes.oVAR_ASSIGN);
            index++;
        }

        GoLiteralIdentifier []identifiers = getIdentifiers();
        int max = index - 1;
        if ( index == 0 || index >= identifiers.length) {
            max = identifiers.length;
        }

        for ( int i = 0; i < max; i++) {
            if ( ! processor.execute(identifiers[i], state) )
                return false;
        }

        return true;
    }
}
