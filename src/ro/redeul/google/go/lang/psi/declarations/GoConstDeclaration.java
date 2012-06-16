package ro.redeul.google.go.lang.psi.declarations;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public interface GoConstDeclaration extends GoPsiElement {

    boolean hasInitializers();

    GoLiteralIdentifier[] getIdentifiers();

    GoExpr[] getExpressions();

}
