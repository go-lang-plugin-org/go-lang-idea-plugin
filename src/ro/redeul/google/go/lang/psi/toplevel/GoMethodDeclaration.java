package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 26, 2010
 * Time: 2:33:21 PM
 */
public interface GoMethodDeclaration extends GoFunctionDeclaration {
    GoPsiElement getMethodReceiver();
}
