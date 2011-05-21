package ro.redeul.google.go.lang.psi.toplevel;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeName;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:56:34 PM
 */
public interface GoTypeSpec extends GoPsiElement {

    @Nullable GoTypeNameDeclaration getTypeNameDeclaration();

    GoType getType();
}
