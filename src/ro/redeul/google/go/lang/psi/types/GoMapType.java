package ro.redeul.google.go.lang.psi.types;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 12:52:20 PM
 */
public interface GoMapType extends GoType {

    GoType getKeyType();

    GoType getElementType();
}
