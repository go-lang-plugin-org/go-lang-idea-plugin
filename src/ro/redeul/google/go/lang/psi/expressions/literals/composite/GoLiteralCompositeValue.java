package ro.redeul.google.go.lang.psi.expressions.literals.composite;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.typing.GoType;

public interface GoLiteralCompositeValue extends GoPsiElement {

    GoLiteralCompositeElement[] getElements();

    @NotNull
    GoType getType();
}
