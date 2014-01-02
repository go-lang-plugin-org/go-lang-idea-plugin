package ro.redeul.google.go.lang.psi.declarations;

import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:23 PM
 */

public interface GoVarDeclarations extends GoPsiElement, GoDocumentedPsiElement {

    GoVarDeclaration[] getDeclarations();

    boolean isMulti();
}
