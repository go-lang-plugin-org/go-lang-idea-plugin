package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
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
        if ( element == identifier ) {
            return true;
        }

        if ( element instanceof GoFunctionDeclaration && ! (element instanceof GoMethodDeclaration) ) {
            return tryResolveToFunction((GoFunctionDeclaration) element, state);
        }

        if ( element instanceof GoVarDeclaration) {
            return tryResolveToIdentifiers(state,
                                           ((GoVarDeclaration) element).getIdentifiers());
        }

        if ( element instanceof GoConstDeclaration) {
            return tryResolveToIdentifiers(state,
                                           ((GoConstDeclaration) element).getIdentifiers());
        }

        if ( element instanceof GoFunctionParameter ) {
            return tryResolveToIdentifiers(state,
                                           ((GoFunctionParameter)element).getIdentifiers());
        }

        if ( element instanceof GoIdentifier) {
            return tryResolveToIdentifiers(state, (GoIdentifier)element);
        }

        return true;
    }

    private boolean tryResolveToLiteralExpression(ResolveState state,
                                                  GoLiteralExpression expression)
    {
        GoLiteral literal = expression.getLiteral();

        if ( literal != null && literal.getType() == GoLiteral.Type.Identifier ) {
            return tryResolveToIdentifiers(state, (GoIdentifier) literal);
        }

        return true;
    }

    private boolean tryResolveToIdentifiers(ResolveState state,
                                            GoIdentifier ... identifiers) {

        for (GoIdentifier identifier : identifiers) {

            if ( this.identifier.getText().equalsIgnoreCase(getVisibleName(identifier.getName(), state)) ) {
                reference = identifier;
                return false;
            }
        }
        return true;
    }

    private String getVisibleName(String name, ResolveState state) {

        String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);

        if ( visiblePackageName != null ) {
            return visiblePackageName + '.' + name;
        }

        return name;

    }

    private boolean tryResolveToFunction(GoFunctionDeclaration functionDeclaration, ResolveState state) {

        String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);

        if ( identifier.getText().equalsIgnoreCase(getVisibleName(functionDeclaration.getFunctionName(), state)) ) {
            reference = functionDeclaration;
            return false;
        }

        return true;
    }

    public PsiElement reference() {
        return reference;
    }
}
