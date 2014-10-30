package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/20/11
 * Time: 1:02 AM
 */
class GoExpressionTypeResolver extends BaseScopeProcessor {

    private final List<GoFunctionDeclaration> nodeFunctions = new ArrayList<GoFunctionDeclaration>();

    public GoExpressionTypeResolver() {
    }

    @Override
    public boolean execute(@NotNull PsiElement element, ResolveState state) {
        if ( element instanceof GoFunctionDeclaration && ! (element instanceof GoMethodDeclaration) ) {

            GoFunctionDeclaration function = (GoFunctionDeclaration) element;

            String functionName = function.getFunctionName();

            if ( GoNamesUtil.isExported(functionName) ) {
                nodeFunctions.add(function);
            }

        }

        return true;
    }

    public List<? extends PsiNamedElement> getFunctions() {
        return nodeFunctions;
    }
}
