package ro.redeul.google.go.lang.psi.types.struct;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 1:54 PM
 */
public interface GoTypeStructAnonymousField extends GoPsiElement {

    @Nullable
    GoPsiType getType();

    String getFieldName();

    GoPsiElementBase getTag();
}
