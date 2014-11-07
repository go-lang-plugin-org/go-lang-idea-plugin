package ro.redeul.google.go.lang.psi.toplevel;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoMethodReceiver extends GoPsiElement {

    @Nullable
    GoLiteralIdentifier getIdentifier();

    boolean isReference();

    GoPsiType getType();

}
