package ro.redeul.google.go.lang.psi.toplevel;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * <p/>
 * Created on Jan-13-2014 23:02
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public interface GoFunctionResult extends GoPsiElement {

    @NotNull
    GoFunctionParameter[] getResults();
}
