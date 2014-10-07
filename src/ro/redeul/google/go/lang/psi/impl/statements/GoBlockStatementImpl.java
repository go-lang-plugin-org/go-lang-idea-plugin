package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoBlockStatementImpl extends GoPsiElementBase implements GoBlockStatement {
    public GoBlockStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoStatement[] getStatements() {
        return findChildrenByClass(GoStatement.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {

        PsiElement node = lastParent != null ? lastParent.getPrevSibling() : this.getLastChild();

        while ( node != null ) {
            if ( GoElementPatterns.BLOCK_DECLARATIONS.accepts(node)) {
                if ( ! node.processDeclarations(processor, state, null, place) ) {
                    return false;
                }
            }
            node = node.getPrevSibling();
        }

        return true;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitBlockStatement(this);
    }
}
