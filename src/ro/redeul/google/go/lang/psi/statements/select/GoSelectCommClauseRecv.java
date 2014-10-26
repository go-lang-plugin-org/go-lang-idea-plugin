/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.statements.select;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoAssignmentStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;

public interface GoSelectCommClauseRecv extends GoSelectCommClause {

//    @NotNull
//    GoExpr[] getVariables();

    @Nullable
    GoAssignmentStatement getAssignment();

    @Nullable
    GoShortVarDeclaration getShortVarDeclaration();

//    boolean isAssignment();
//
//    boolean isDeclaration();

    @Nullable
    GoExpr getReceiveExpression();
}
