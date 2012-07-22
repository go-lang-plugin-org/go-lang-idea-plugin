package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:43:12 PM
 */
public interface GoPackageReference extends GoPsiElement, PsiReference, PsiNamedElement {

    String getString();

    boolean isBlank();

    boolean isLocal();
}
