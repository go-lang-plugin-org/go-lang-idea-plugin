package ro.redeul.google.go.lang.psi.types;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoQualifiedNameElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 7:11:26 PM
 */
public interface GoTypeName extends GoPsiElement, PsiNamedElement,
                                    GoQualifiedNameElement, GoType
{
    @NotNull
    GoLiteralIdentifier getIdentifier();

    boolean isReference();

    boolean isPrimitive();
}
