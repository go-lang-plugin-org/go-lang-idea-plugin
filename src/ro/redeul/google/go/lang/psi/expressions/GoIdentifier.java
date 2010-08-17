package ro.redeul.google.go.lang.psi.expressions;

import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:43:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoIdentifier extends GoPsiElement {

    String getString();

    boolean isBlank();
}
