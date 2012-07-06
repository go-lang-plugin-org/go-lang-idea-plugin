package ro.redeul.google.go.lang.psi.types;

import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 2:14 PM
 */
public interface GoTypeInterface extends GoType, GoMethodSetHolder {

    GoMethodDeclaration[] getMethodDeclarations();

    GoTypeName[] getTypeNames();
}
