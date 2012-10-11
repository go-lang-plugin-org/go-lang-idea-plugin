package ro.redeul.google.go.lang.psi.toplevel;

import com.intellij.psi.PsiNamedElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 1:42:25 PM
 */
public interface GoTypeNameDeclaration extends GoPsiElement, PsiNamedElement {

    String getPackageName();

    GoTypeSpec getTypeSpec();

    String getCanonicalName();
}
