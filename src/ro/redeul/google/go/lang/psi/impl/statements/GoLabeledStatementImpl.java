package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoLabeledStatement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLabeledStatementImpl extends GoPsiElementBase implements GoLabeledStatement {
    public GoLabeledStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public GoLiteralIdentifier getLabel() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    @Nullable
    @Override
    public GoStatement getStatement() {
        return findChildByClass(GoStatement.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLabeledStatement(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        GoStatement statement = getStatement();

        if ( statement != null ) {
            return statement.processDeclarations(processor, state,
                                                 lastParent, place);
        }

        return true;
    }
}
