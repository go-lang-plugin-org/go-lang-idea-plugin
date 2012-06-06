package ro.redeul.google.go.lang.psi.expressions.literals;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

public interface GoLiteral extends GoExpr {

    GoIdentifier getIdentifier();

}
