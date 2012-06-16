package ro.redeul.google.go.lang.psi.toplevel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:54:27 PM
 */
public interface GoImportDeclaration extends GoPsiElement {

    @Nullable
    GoPackageReference getPackageReference();

    @Nullable
    String getImportPath();

    String getPackageName();

    @NotNull
    String getVisiblePackageName();
}
