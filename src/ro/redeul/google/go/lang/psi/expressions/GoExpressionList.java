/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.expressions;

import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Go Psi element that has a list of expressions as children.
 */
public interface GoExpressionList extends GoPsiElement {

    GoExpr[] getExpressions();

}
