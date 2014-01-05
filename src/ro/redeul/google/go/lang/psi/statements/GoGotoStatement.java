package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public interface GoGotoStatement extends GoPsiElement, GoStatement {

    GoLiteralIdentifier getLabel();

}
