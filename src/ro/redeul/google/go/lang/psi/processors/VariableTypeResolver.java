package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoLiteralExpressionImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 7:38 PM
 */
public class VariableTypeResolver extends BaseScopeProcessor {

    private GoType type;
    private GoIdentifier identifier;

    public VariableTypeResolver(GoIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean execute(PsiElement element, ResolveState state) {

        if ( element instanceof GoFunctionParameter ) {
            return checkFunctionParameters((GoFunctionParameter) element);
        }

        return true;
    }

    private boolean checkFunctionParameters(GoFunctionParameter parameter) {

        GoIdentifier []identifiers = parameter.getIdentifiers();

        for (GoIdentifier identifier : identifiers) {
            if ( identifier.getText().equalsIgnoreCase(this.identifier.getText()) ) {
                type = parameter.getType();
                return false;
            }
        }

        return true;
    }

    public GoType getResolvedType() {
        return type;
    }
}
