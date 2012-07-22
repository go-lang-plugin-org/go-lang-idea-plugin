package ro.redeul.google.go.lang.psi.expressions.literals.composite;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoLiteralComposite extends GoLiteral<GoLiteralCompositeValue> {

    GoPsiType getLiteralType();

    @NotNull
    GoLiteralCompositeValue getValue();
}
