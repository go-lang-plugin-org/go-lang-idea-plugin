/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements.select;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoSelectCommClauseRecv extends GoSelectCommClause {

    @NotNull
    GoExpr[] getVariables();

    boolean isAssignment();

    boolean isDeclaration();

    GoExpr getReceiveExpression();
}
