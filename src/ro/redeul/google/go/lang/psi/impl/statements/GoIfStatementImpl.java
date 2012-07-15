package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoIfStatement;
import ro.redeul.google.go.lang.psi.statements.GoSimpleStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoIfStatementImpl extends GoPsiElementBase
    implements GoIfStatement {
    public GoIfStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoSimpleStatement getSimpleStatement() {
        return findChildByClass(GoSimpleStatement.class);
    }

    @Override
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public GoBlockStatement getThenBlock() {
        return findChildByClass(GoBlockStatement.class);
    }

    @Override
    public GoIfStatement getElseIfStatement() {
        return findChildByClass(GoIfStatement.class);
    }

    @Override
    public GoBlockStatement getElseBlock() {
        return findChildByClass(GoBlockStatement.class, 1);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if (lastParent == null)
            return true;

        GoSimpleStatement statement = getSimpleStatement();
        if (statement != null && lastParent != statement) {
            if (!statement.processDeclarations(processor, state, null, place))
                return false;
        }

        return true;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitIfStatement(this);
    }
}
