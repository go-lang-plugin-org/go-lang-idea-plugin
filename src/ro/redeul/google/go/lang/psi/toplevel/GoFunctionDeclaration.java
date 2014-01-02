package ro.redeul.google.go.lang.psi.toplevel;

import com.intellij.psi.PsiNameIdentifierOwner;
import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 26, 2010
 * Time: 2:32:15 PM
 */
public interface GoFunctionDeclaration extends GoPsiElement, PsiNameIdentifierOwner,
    GoPsiTypeFunction, GoDocumentedPsiElement {

    String getFunctionName();

    boolean isMain();

    GoBlockStatement getBlock();

    //    GoFunctionParameterList getParameters();
    GoFunctionParameter[] getParameters();

    GoFunctionParameter[] getResults();

    GoPsiType[] getReturnType();
}
