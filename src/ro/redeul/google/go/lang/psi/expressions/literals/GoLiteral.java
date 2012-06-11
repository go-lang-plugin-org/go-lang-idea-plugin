package ro.redeul.google.go.lang.psi.expressions.literals;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;

public interface GoLiteral<T> extends GoPsiElement {

    public enum Type {
        RawString, InterpretedString,
        Bool, Char,
        Int, ImaginaryInt,
        Float, Identifier, ImaginaryFloat,
        Composite, Function
    }

    @Nullable
    T getValue();

    Type getType();
}
