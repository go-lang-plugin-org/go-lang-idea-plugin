package ro.redeul.google.go.lang.psi.expressions;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 4, 2010
 * Time: 10:42:42 PM
 */
public interface GoIdentifier extends GoPsiExpression, PsiReference, PsiNamedElement {

    boolean isBlank();
}
