package ro.redeul.google.go.lang.psi.types;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoQualifiedNameElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoLiteralIdentifierImpl;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 7:11:26 PM
 */
public interface GoPsiTypeName extends GoPsiElement, PsiNamedElement,
                                    GoQualifiedNameElement, GoPsiType
{
    @NotNull
    GoLiteralIdentifier getIdentifier();

    boolean isQualified();

    @Nullable
    GoLiteralIdentifier getQualifier();

    boolean isPrimitive();
}
