package ro.redeul.google.go.lang.psi.declarations;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;

public interface GoVarDeclaration extends GoPsiElement {

    GoIdentifier[] getIdentifiers();

    GoExpr[] getExpressions();

}
