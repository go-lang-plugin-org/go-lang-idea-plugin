package ro.redeul.google.go.lang.psi.toplevel;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:56:34 PM
 */
public interface GoTypeSpec extends GoDocumentedPsiElement, PsiNamedElement {

    @Nullable GoTypeNameDeclaration getTypeNameDeclaration();

    GoPsiType getType();
}
