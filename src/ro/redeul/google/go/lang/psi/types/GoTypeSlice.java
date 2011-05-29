package ro.redeul.google.go.lang.psi.types;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 12:50:07 PM
 */
public interface GoTypeSlice extends GoType {

    GoType getElementType();

}
