package ro.redeul.google.go.lang.psi.expressions.literals;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 4, 2010
 * Time: 10:42:42 PM
 */
public interface GoIdentifier extends GoPsiElement, PsiReference, PsiNamedElement {

    boolean isBlank();

    boolean isIota();
}
