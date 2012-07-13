package ro.redeul.google.go.lang.psi.resolve.references;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypePointer;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class SelectorOfStructFieldReference
    extends AbstractStructFieldsReference {

    GoSelectorExpression selectorExpression;

    public SelectorOfStructFieldReference(GoSelectorExpression expression) {
        super(expression.getIdentifier());

        selectorExpression = expression;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
    }

    @Override
    protected GoTypeStruct resolveTypeDefinition() {
        GoPrimaryExpression baseExpression = selectorExpression.getBaseExpression();
        if (baseExpression == null)
            return null;

        GoType[] types = baseExpression.getType();
        if (types.length == 0)
            return null;

        GoType type = types[0];

        while (type != null && !(type instanceof GoTypeStruct)) {
            if (type instanceof GoTypePointer)
                type = ((GoTypePointer) type).getTargetType();

            GoTypeSpec typeSpec =
                GoPsiUtils.resolveSafely(type, GoTypeSpec.class);

            if (typeSpec != null) {
                type = typeSpec.getType();
            }
        }

        if (type == null)
            return null;

        return (GoTypeStruct) type;
    }
}
