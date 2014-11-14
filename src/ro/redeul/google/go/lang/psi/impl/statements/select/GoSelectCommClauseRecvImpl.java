package ro.redeul.google.go.lang.psi.impl.statements.select;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.statements.GoAssignmentStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseRecv;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoSelectCommClauseRecvImpl extends GoPsiElementBase implements GoSelectCommClauseRecv {

    public GoSelectCommClauseRecvImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public GoAssignmentStatement getAssignment() {
        return findChildByClass(GoAssignmentStatement.class);
    }

    @Nullable
    @Override
    public GoShortVarDeclaration getShortVarDeclaration() {
        return findChildByClass(GoShortVarDeclaration.class);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public GoExpr getReceiveExpression() {

        GoExpr expr = findChildByClass(GoExpr.class);
        if (expr != null)
            return expr;

        GoAssignmentStatement assignmentStatement = getAssignment();
        if (assignmentStatement != null) {
            GoExpressionList expressionList = assignmentStatement.getRightSideExpressions();
            if (expressionList.getExpressions() != null && expressionList.getExpressions().length > 0)
                return expressionList.getExpressions()[0];

            return expr;
        }

        GoShortVarDeclaration declaration = getShortVarDeclaration();
        if (declaration != null) {
            GoExpr expressions[] = declaration.getExpressions();
            if (expressions.length > 0)
                return expressions[0];
        }

        return expr;
    }

    @Override
    public GoStatement[] getStatements() {
        return findChildrenByClass(GoStatement.class);
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSelectCommClauseRecv(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        PsiElement node = lastParent != null ? lastParent.getPrevSibling() : null;

        while (node != null) {
            if (GoElementPatterns.BLOCK_DECLARATIONS.accepts(node)) {
                if (!node.processDeclarations(processor, state, null, place)) {
                    return false;
                }
            }
            node = node.getPrevSibling();
        }

//        if ( isDeclaration() && lastParent != this ) {
//            GoExpr[] variables = getVariables();
//            for (int i = variables.length - 1; i >= 0; i--) {
//                GoExpr expr = variables[i];
//                if (expr instanceof GoLiteralExpression ) {
//                    GoLiteralExpression literalExpression = (GoLiteralExpression) expr;
//                    if (!processor.execute(literalExpression.getLiteral(), state))
//                        return false;
//                }
//            }
//        }

        return true;
    }
}
