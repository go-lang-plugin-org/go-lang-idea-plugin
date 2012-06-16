package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoTypeName;

public interface GoMethodReceiver extends GoPsiElement {

    GoLiteralIdentifier getIdentifier();

    boolean isReference();

    GoTypeName getTypeName();

}
