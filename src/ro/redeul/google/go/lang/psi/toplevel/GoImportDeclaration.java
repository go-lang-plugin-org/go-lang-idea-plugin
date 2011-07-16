package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoPackageReference;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:54:27 PM
 */
public interface GoImportDeclaration extends GoPsiElement {

    GoPackageReference getPackageReference();

    String getImportPath();

    String getPackageName();

    String getVisiblePackageName();
}
