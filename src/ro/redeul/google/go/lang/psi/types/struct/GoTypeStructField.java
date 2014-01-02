package ro.redeul.google.go.lang.psi.types.struct;

import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 12:26 PM
 */
public interface GoTypeStructField extends GoPsiElement, GoDocumentedPsiElement {

    boolean isBlank();

    GoLiteralIdentifier[] getIdentifiers();

    GoPsiType getType();

}
