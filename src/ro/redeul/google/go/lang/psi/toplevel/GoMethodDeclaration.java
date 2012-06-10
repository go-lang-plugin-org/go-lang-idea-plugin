package ro.redeul.google.go.lang.psi.toplevel;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 26, 2010
 * Time: 2:33:21 PM
 */
public interface GoMethodDeclaration extends GoFunctionDeclaration {
    GoMethodReceiver getMethodReceiver();
}
