package ro.redeul.google.go.lang.psi.expressions.literals.composite;

import ro.redeul.google.go.lang.psi.GoPsiElement;

public interface GoLiteralCompositeValue extends GoPsiElement {
    GoLiteralCompositeElement[] getElements();
}
