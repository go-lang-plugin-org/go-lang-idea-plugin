package ro.redeul.google.go.lang.psi.statements;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoIfStatement extends GoStatement {
    PsiElement getSimpleStatement();

    GoExpr getCondition();

    GoBlockStatement getThenClause();

    /**
     * Get "else" clause of "if" statement.
     * @return the else clause could be one of: null, GoIfStatement or GoBlockStatement
     */
    GoStatement getElseClause();
}
