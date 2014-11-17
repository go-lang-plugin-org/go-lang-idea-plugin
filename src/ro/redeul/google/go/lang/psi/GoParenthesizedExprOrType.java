package ro.redeul.google.go.lang.psi;

import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeParenthesized;

public interface GoParenthesizedExprOrType extends GoParenthesisedExpression, GoPsiTypeParenthesized {
    public boolean isType();
}
