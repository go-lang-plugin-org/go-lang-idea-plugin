package ro.redeul.google.go.lang.psi.types;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 9:07:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoArrayType extends GoType {

//    GoExpression getArrayLength();

    GoType getElementType();
}
