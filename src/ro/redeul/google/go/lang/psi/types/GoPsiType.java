package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.GoPackagedElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:58:40 PM
 */
public interface GoPsiType extends GoPsiElement, GoPackagedElement {

    public static final GoPsiType[] EMPTY_ARRAY = new GoPsiType[0];

    GoUnderlyingType getUnderlyingType();

    boolean isIdentical(GoPsiType goType);
}
