package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 7:38 PM
 */
class VariableTypeResolver extends BaseScopeProcessor {

    private GoPsiType type;
    private final GoLiteralIdentifier identifier;

    public VariableTypeResolver(GoLiteralIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean execute(@NotNull PsiElement element, ResolveState state) {

        if ( element instanceof GoFunctionParameter ) {
            return checkFunctionParameters((GoFunctionParameter) element);
        }

        if ( element instanceof GoShortVarDeclaration) {
            GoShortVarDeclaration shortVarDeclaration = (GoShortVarDeclaration) element;

            GoLiteralIdentifier identifiers[] = shortVarDeclaration.getIdentifiers();

            for (GoLiteralIdentifier identifier : identifiers) {

                if (identifier.getName() == null) {
                    return false;
                }

                if (identifier.getName().equalsIgnoreCase(this.identifier.getName())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkFunctionParameters(GoFunctionParameter parameter) {

        GoLiteralIdentifier[]identifiers = parameter.getIdentifiers();

        for (GoLiteralIdentifier identifier : identifiers) {
            if ( identifier.getText().equalsIgnoreCase(this.identifier.getText()) ) {
                type = parameter.getType();
                return false;
            }
        }

        return true;
    }

    public GoPsiType getResolvedType() {
        return type;
    }
}
