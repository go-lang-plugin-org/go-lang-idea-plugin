package ro.redeul.google.go.lang.psi.toplevel;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:29:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GoPackageDeclaration extends GoPsiElement, GoDocumentedPsiElement {

    @NotNull
    String getPackageName();

    boolean isMainPackage();
}
