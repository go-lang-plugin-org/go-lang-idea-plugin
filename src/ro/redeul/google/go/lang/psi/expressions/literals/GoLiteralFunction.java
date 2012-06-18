package ro.redeul.google.go.lang.psi.expressions.literals;

import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

public interface GoLiteralFunction extends GoLiteral<GoFunctionDeclaration>,
                                           GoFunctionDeclaration
{
    GoFunctionParameter[] getParameters();

    GoFunctionParameter[] getResults();

    GoBlockStatement getBlock();
}
