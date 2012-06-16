package ro.redeul.google.go.lang.psi.expressions.literals;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nullable;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 4, 2010
 * Time: 10:42:42 PM
 */
public interface GoLiteralIdentifier extends GoLiteral<String>, PsiReference, PsiNameIdentifierOwner {

    boolean isBlank();

    boolean isIota();

    /**
     * @return true if the identifier is a global variable or global constant.
     */
    boolean isGlobal();

    boolean isQualified();

    @Nullable
    String getLocalPackageName();
}
