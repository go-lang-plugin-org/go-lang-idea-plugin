package ro.redeul.google.go.lang.psi.types;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 12:52:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoMapType extends GoType {

    GoType getKeyType();

    GoType getElementType();
}
