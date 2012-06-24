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
public interface GoType extends GoPsiElement, GoPackagedElement {

    public static final GoType[] EMPTY_ARRAY = new GoType[0];

    GoPsiElement[] getMembers();

    GoType getMemberType(String name);

    GoUnderlyingType getUnderlyingType();

    boolean isIdentical(GoType goType);
}
