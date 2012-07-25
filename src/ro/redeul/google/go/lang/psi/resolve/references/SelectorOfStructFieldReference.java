package ro.redeul.google.go.lang.psi.resolve.references;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

public class SelectorOfStructFieldReference extends AbstractStructFieldsReference {

    GoSelectorExpression selectorExpression;

    public SelectorOfStructFieldReference(GoSelectorExpression expression) {
        super(expression.getIdentifier());

        selectorExpression = expression;
    }

    @Override
    protected AbstractStructFieldsReference self() {
        return this;
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
            if (type instanceof GoTypePointer) {
                type = ((GoTypePointer)type).getTargetType();
            } else if ( type instanceof GoTypeName ) {
                type = ((GoTypeName) type).getDefinition();
            } else {
                type = null;
            }
        }

        if (type == null)
            return null;

        return (GoTypeStruct) type;
    }
}
