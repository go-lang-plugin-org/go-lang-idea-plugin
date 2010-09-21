package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:43:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoPackageReference extends GoPsiElement, PsiReference, PsiNamedElement {

    String getString();

    boolean isBlank();

    boolean isLocal();
}
