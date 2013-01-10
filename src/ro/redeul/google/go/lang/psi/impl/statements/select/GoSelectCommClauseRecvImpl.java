/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.impl.statements.select;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseRecv;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoSelectCommClauseRecvImpl extends GoPsiElementBase
    implements GoSelectCommClauseRecv {
    public GoSelectCommClauseRecvImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public GoExpr[] getVariables() {
        return findChildrenByClass(GoExpr.class);
    }

    @Override
    public boolean isAssignment() {
        return findChildByType(GoTokenTypes.oASSIGN) != null;
    }

    @Override
    public boolean isDeclaration() {
        return findChildByType(GoTokenTypes.oVAR_ASSIGN) != null;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public GoExpr getReceiveExpression() {
        PsiElement receiverExpressionHolder =findChildByType(GoElementTypes.SELECT_COMM_CLAUSE_RECV_EXPR);

        if (receiverExpressionHolder == null)
            return null;

        return GoPsiUtils.findChildOfClass(receiverExpressionHolder, GoExpr.class);
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
        if ( isDeclaration() ) {
            GoExpr[] variables = getVariables();
            for (int i = variables.length - 1; i >= 0; i--) {
                GoExpr expr = variables[i];
                if (expr instanceof GoLiteralExpression ) {
                    GoLiteralExpression literalExpression = (GoLiteralExpression) expr;
                    if (!processor.execute(literalExpression.getLiteral(), state))
                        return false;
                }
            }
        }

        return true;
    }
}
