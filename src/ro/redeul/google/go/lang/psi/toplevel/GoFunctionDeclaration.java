package ro.redeul.google.go.lang.psi.toplevel;

import com.intellij.psi.PsiNamedElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 26, 2010
 * Time: 2:32:15 PM
 */
public interface GoFunctionDeclaration extends GoPsiElement, PsiNamedElement {

    String getFunctionName();

    boolean isMain();

    GoBlockStatement getBlock();

//    GoFunctionParameterList getParameters();
    GoFunctionParameter[] getParameters();

    GoFunctionParameter[] getResults();
}
