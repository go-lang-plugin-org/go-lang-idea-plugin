package ro.redeul.google.go.lang.psi.toplevel;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 26, 2010
 * Time: 2:32:15 PM
 */
public interface GoFunctionDeclaration extends GoPsiElement, PsiNameIdentifierOwner,
                                               GoPsiTypeFunction {

    @NotNull
    @Override
    String getName();

    String getFunctionName();

    boolean isMain();

    boolean isInit();

    GoBlockStatement getBlock();

    GoFunctionParameter[] getParameters();

    GoFunctionParameter[] getResults();

    /**
     * This will return an array of types for each positional parameter regardless of how they are defined
     */
    @NotNull
    GoType[] getParameterTypes();

    @NotNull
    GoType[] getReturnTypes();
}
