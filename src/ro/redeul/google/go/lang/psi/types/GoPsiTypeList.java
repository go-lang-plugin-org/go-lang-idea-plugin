package ro.redeul.google.go.lang.psi.types;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElementList;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-13-2014 22:51
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public interface GoPsiTypeList extends GoPsiElement, GoPsiElementList<GoPsiType> {

    @NotNull
    GoPsiType[] getTypes();
}
