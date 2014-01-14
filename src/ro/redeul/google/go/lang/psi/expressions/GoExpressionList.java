package ro.redeul.google.go.lang.psi.expressions;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElementList;

public interface GoExpressionList extends GoPsiElement, GoPsiElementList<GoExpr> {

    @NotNull
    GoExpr[] getExpressions();
}
