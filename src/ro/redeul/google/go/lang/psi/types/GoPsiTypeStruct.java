package ro.redeul.google.go.lang.psi.types;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;

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

    GoTypeStructAnonymousField[] getAnonymousFields();
}
