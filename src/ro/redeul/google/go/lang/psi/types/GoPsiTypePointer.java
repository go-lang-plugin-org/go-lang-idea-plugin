package ro.redeul.google.go.lang.psi.types;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:52 PM
 */
public interface GoPsiTypePointer extends GoPsiType {

    GoPsiType getTargetType();

}
