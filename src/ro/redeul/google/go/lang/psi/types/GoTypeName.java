package ro.redeul.google.go.lang.psi.types;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoQualifiedNameElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 7:11:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoTypeName extends GoPsiElement, PsiNamedElement, PsiReference, GoQualifiedNameElement {
}
