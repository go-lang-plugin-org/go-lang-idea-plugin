package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:52:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoImportDeclaration extends GoPsiElement {

    GoImportSpec[] getImports();
}
