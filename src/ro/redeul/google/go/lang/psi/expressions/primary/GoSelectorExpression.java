package ro.redeul.google.go.lang.psi.expressions.primary;

import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:56 PM
 */
public interface GoSelectorExpression extends GoPrimaryExpression {

    GoPrimaryExpression getBaseExpression();

    GoLiteralIdentifier getIdentifier();
}
