package ro.redeul.google.go.lang.psi.expressions.literals.composite;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

public interface GoLiteralCompositeElement extends GoPsiElement {

    @Nullable
    GoLiteralIdentifier getKey();

    @Nullable
    GoExpr getIndex();

    @Nullable
    GoExpr getExpressionValue();

    @Nullable
    GoLiteralCompositeValue getLiteralValue();
}
