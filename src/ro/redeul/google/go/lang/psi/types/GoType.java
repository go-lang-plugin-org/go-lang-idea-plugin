package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.GoPackagedElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:58:40 PM
 */
public interface GoType extends GoPsiElement, GoPackagedElement {

    GoPsiElement[] getMembers();

    GoType getMemberType(String name);

}
