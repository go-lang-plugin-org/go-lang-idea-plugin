package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoStatement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:56:01 PM
 */
public interface GoTypeDeclaration extends GoPsiElement, GoStatement, GoDocumentedPsiElement {

    GoTypeSpec[] getTypeSpecs();

    boolean isMulti();
}
