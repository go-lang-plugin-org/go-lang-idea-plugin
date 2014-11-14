package ro.redeul.google.go.lang.psi.statements.select;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoAssignmentStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;

public interface GoSelectCommClauseRecv extends GoSelectCommClause {

    @Nullable
    GoAssignmentStatement getAssignment();

    @Nullable
    GoShortVarDeclaration getShortVarDeclaration();

    @Nullable
    GoExpr getReceiveExpression();
}
