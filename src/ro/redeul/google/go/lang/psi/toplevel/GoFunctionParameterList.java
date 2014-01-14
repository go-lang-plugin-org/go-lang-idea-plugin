package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElementList;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:00 PM
 */
public interface GoFunctionParameterList extends GoPsiElement, GoPsiElementList<GoFunctionParameter> {

    GoFunctionParameter[] getFunctionParameters();

}
