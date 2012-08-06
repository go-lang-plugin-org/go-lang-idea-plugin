package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

public class SelectorOfStructFieldReference
    extends AbstractStructFieldsReference<GoSelectorExpression, SelectorOfStructFieldReference> {

    public SelectorOfStructFieldReference(GoSelectorExpression expression) {
        super(expression, expression.getIdentifier(), RESOLVER);
    }

    private static final ResolveCache.AbstractResolver<SelectorOfStructFieldReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<SelectorOfStructFieldReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(SelectorOfStructFieldReference psiReference, boolean incompleteCode) {

                GoTypeStruct typeStruct = psiReference.resolveTypeDefinition();

                if ( typeStruct == null || typeStruct.getPsiType() == null)
                    return null;

                GoLiteralIdentifier element = psiReference.getReferenceElement();

                for (GoTypeStructField field : typeStruct.getPsiType().getFields()) {
                    for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                        if (identifier.getUnqualifiedName().equals(element.getUnqualifiedName()))
                            return new GoResolveResult(identifier);
                    }
                }

                for (GoTypeStructAnonymousField field : typeStruct.getPsiType().getAnonymousFields()) {
                    if (field.getFieldName().equals(element.getUnqualifiedName()))
                        return new GoResolveResult(field);
                }

                return GoResolveResult.NULL;
            }
        };

    @Override
    protected SelectorOfStructFieldReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getReferenceElement().getCanonicalName();
    }

    @Override
    protected GoTypeStruct resolveTypeDefinition() {
        GoPrimaryExpression baseExpression = getElement().getBaseExpression();
        if (baseExpression == null)
            return null;

        GoType[] types = baseExpression.getType();
        if (types.length == 0)
            return null;

        GoType type = types[0];

        while (type != null && !(type instanceof GoTypeStruct)) {
            if (type instanceof GoTypePointer) {
                type = ((GoTypePointer) type).getTargetType();
            } else if (type instanceof GoTypeName) {
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
