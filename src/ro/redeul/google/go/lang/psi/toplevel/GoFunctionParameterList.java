package ro.redeul.google.go.lang.psi.toplevel;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:00 PM
 */
public interface GoFunctionParameterList extends GoPsiElement {

    GoFunctionParameter[] getFunctionParameters();

}
