package ro.redeul.google.go.lang.psi.expressions.literals;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

public interface GoLiteralFunction extends GoExpr {

    GoFunctionParameter[] getParameters();

    GoFunctionParameter[] getResults();

    GoBlockStatement getBlock();
}
