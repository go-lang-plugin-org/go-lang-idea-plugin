package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 2:14 PM
 */
public interface GoPsiTypeInterface extends GoPsiType, GoMethodSetHolder {

    GoFunctionDeclaration[] getFunctionDeclarations();

    GoPsiTypeName[] getTypeNames();
}
