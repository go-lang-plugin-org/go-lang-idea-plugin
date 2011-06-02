package ro.redeul.google.go.lang.psi.expressions.literals;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 11:07 PM
 */
public interface GoLiteralExpression extends GoExpr {

    GoIdentifier getIdentifier();

}
