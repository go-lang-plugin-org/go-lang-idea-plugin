package ro.redeul.google.go.lang.psi;

import com.intellij.psi.PsiNamedElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:03 PM
 */
public interface GoPackagedElement extends PsiNamedElement {

    String getPackageName();

    String getQualifiedName();

}
