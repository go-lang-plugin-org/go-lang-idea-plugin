package ro.redeul.google.go.lang.psi.types;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 12:50:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoSliceType extends GoType {

    GoType getElementType();

}
