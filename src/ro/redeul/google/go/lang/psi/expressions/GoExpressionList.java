package ro.redeul.google.go.lang.psi.expressions;

import ro.redeul.google.go.lang.psi.GoPsiElement;

// TODO: Remove this as it's not needed in general
public interface GoExpressionList extends GoPsiElement {
    GoExpr[] getExpressions();
}
