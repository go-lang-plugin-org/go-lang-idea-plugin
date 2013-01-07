/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements.select;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public interface GoSelectCommClauseRecv extends GoSelectCommClause {

    @NotNull
    GoLiteralIdentifier[] getVariables();

    boolean isAssignment();

    boolean isDeclaration();

    @NotNull
    GoExpr getReceiveExpression();
}
