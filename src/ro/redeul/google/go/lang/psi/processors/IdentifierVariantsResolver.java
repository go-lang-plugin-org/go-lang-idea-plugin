package ro.redeul.google.go.lang.psi.processors;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.GoIdentifierImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/22/11
 * Time: 8:35 PM
 */
public class IdentifierVariantsResolver extends BaseScopeProcessor {

    PsiElement reference;
    GoIdentifier identifier;

    public IdentifierVariantsResolver(GoIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean execute(PsiElement element, ResolveState state) {
        if ( element instanceof GoFunctionDeclaration && ! (element instanceof GoMethodDeclaration) ) {
            tryResolveToFunction((GoFunctionDeclaration) element, state);
        }

        return true;
    }

    private boolean tryResolveToFunction(GoFunctionDeclaration functionDeclaration, ResolveState state) {

        String functionName = functionDeclaration.getFunctionName();

        String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);

        String finalName = functionName;
        if ( visiblePackageName != null ) {
            finalName = visiblePackageName + '.' + functionName;
        }

        if ( identifier.getText().equalsIgnoreCase(finalName) ) {
            reference = functionDeclaration;
            return false;
        }

        return true;
    }

    public PsiElement reference() {
        return reference;
    }
}
