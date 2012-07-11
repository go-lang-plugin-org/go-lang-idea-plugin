package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public interface GoLabeledStatement extends GoStatement {
    GoLiteralIdentifier getLabel();

    GoStatement getStatement();
}
