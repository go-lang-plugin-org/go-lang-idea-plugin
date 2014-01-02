package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:52:43 PM
 */
public interface GoImportDeclarations extends GoPsiElement, GoDocumentedPsiElement {

  GoImportDeclaration[] getDeclarations();

  boolean isMulti();

}
