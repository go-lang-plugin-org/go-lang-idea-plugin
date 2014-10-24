package ro.redeul.google.go.lang.psi.impl.statements.switches;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchExpressionClause;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoSwitchExpressionClauseImpl extends GoPsiElementBase
    implements GoSwitchExpressionClause {

    public GoSwitchExpressionClauseImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isDefault() {
        return findChildByType(GoTokenTypes.kDEFAULT) != null;
    }

    @NotNull
    @Override
    public GoExpr[] getExpressions() {
        return findChildrenByClass(GoExpr.class);
    }

    @NotNull
    @Override
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

    public void accept(GoElementVisitor visitor) {
        visitor.visitSwitchExpressionClause(this);
    }
}
