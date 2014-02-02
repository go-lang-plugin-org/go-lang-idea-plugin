package ro.redeul.google.go.lang.psi.types;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/27/11
 * Time: 12:18 AM
 */
public interface GoPsiTypeStruct extends GoPsiType {

    GoTypeStructField[] getFields();

    @NotNull
    GoTypeStructPromotedFields getPromotedFields();

    //TODO: maybe we should create a GoTypeStructFieldBase interface, will be better than returns PsiElements
    PsiElement[] getAllFields();

    GoTypeStructAnonymousField[] getAnonymousFields();
}
