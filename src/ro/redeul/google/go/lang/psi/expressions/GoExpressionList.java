package ro.redeul.google.go.lang.psi.expressions;

import ro.redeul.google.go.lang.psi.GoPsiElement;

public interface GoExpressionList extends GoPsiElement {
    GoExpr[] getExpressions();
}
