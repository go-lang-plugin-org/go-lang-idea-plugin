package ro.redeul.google.go.lang.psi.types;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 9:07:04 PM
 */
public interface GoPsiTypeArray extends GoPsiType {

    int getArrayLength();

    GoPsiType getElementType();
}
