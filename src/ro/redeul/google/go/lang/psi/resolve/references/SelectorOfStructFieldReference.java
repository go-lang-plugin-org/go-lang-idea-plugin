package ro.redeul.google.go.lang.psi.resolve.references;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.typing.GoType;
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
    protected GoPsiTypeStruct resolveTypeDefinition() {
        GoPrimaryExpression baseExpression = selectorExpression.getBaseExpression();
        if (baseExpression == null)
            return null;

        GoType[] types = baseExpression.getType();
        if (types.length == 0)
            return null;

        GoType type = types[0];

        while (type != null && !(type instanceof GoTypeStruct)) {
            if (type instanceof GoTypePointer)
                type = ((GoTypePointer)type).getTargetType();

//            GoTypeSpec typeSpec =
//                GoPsiUtils.resolveSafely(type.getPsiType(), GoTypeSpec.class);

//            if (typeSpec != null) {
    // TODO: fix compilation here
//                type = typeSpec.getType();
//            }
        }

        if (type == null)
            return null;

        return (GoPsiTypeStruct) type;
    }
}
