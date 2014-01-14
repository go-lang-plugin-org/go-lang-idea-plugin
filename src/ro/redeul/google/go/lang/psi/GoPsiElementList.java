package ro.redeul.google.go.lang.psi;

import org.jetbrains.annotations.NotNull;

/**
 * <p/>
 * Created on Jan-13-2014 22:47
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public interface GoPsiElementList<Element extends GoPsiElement> extends GoPsiElement {

    @NotNull
    Element[] getElements();
}
