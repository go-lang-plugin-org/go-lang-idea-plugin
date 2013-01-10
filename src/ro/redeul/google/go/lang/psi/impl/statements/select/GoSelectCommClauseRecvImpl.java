/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.impl.statements.select;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.select.GoSelectCommClauseRecv;
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
    public GoLiteralIdentifier[] getVariables() {
        return findChildrenByClass(GoLiteralIdentifier.class);
    }

    @Override
    public boolean isAssignment() {
        return findChildByType(GoTokenTypes.oASSIGN) != null;
    }

    @Override
    public boolean isDeclaration() {
        return findChildByType(GoTokenTypes.oVAR_ASSIGN) != null;
    }

    @NotNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public GoExpr getReceiveExpression() {
        return findChildByClass(GoExpr.class);
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
}
