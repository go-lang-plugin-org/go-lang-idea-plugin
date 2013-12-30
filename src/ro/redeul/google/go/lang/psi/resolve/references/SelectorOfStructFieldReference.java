package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

import static ro.redeul.google.go.lang.psi.typing.GoTypes.resolveToStruct;

public class SelectorOfStructFieldReference
    extends AbstractStructFieldsReference<GoSelectorExpression, SelectorOfStructFieldReference> {

    public SelectorOfStructFieldReference(GoSelectorExpression expression) {
        super(expression, expression.getIdentifier(), RESOLVER);
    }

    private static final ResolveCache.AbstractResolver<SelectorOfStructFieldReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<SelectorOfStructFieldReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(@NotNull SelectorOfStructFieldReference psiReference, boolean incompleteCode) {

                GoTypeStruct typeStruct = psiReference.resolveTypeDefinition();

                if ( typeStruct == null || typeStruct.getPsiType() == null)
                    return null;

                GoLiteralIdentifier element = psiReference.getReferenceElement();
                GoPsiTypeStruct type = typeStruct.getPsiType();
                String unqualifiedName = element.getUnqualifiedName();
                GoResolveResult result = findDirectFieldOfName(type, unqualifiedName);
                if (result == GoResolveResult.NULL) {
                    result = findPromotedFieldOfName(type, unqualifiedName);
                }

                return result;
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

        return resolveToStruct(types[0]);
    }

    private static GoResolveResult findPromotedFieldOfName(GoPsiTypeStruct type, String unqualifiedName) {
        if (type == null || StringUtil.isEmpty(unqualifiedName)) {
            return GoResolveResult.NULL;
        }

        GoTypeStructPromotedFields promotedFields = type.getPromotedFields();
        for (GoLiteralIdentifier identifier : promotedFields.getNamedFields()) {
            if (unqualifiedName.equals(identifier.getUnqualifiedName())) {
                return GoResolveResult.fromElement(identifier);
            }
        }

        for (GoTypeStructAnonymousField field : promotedFields.getAnonymousFields()) {
            if (unqualifiedName.equals(field.getFieldName())) {
                return GoResolveResult.fromElement(field);
            }
        }
        return GoResolveResult.NULL;
    }

    private static GoResolveResult findDirectFieldOfName(GoPsiTypeStruct type, String unqualifiedName) {
        if (type == null) {
            return GoResolveResult.NULL;
        }

        for (GoTypeStructField field : type.getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                if (identifier.getUnqualifiedName().equals(unqualifiedName))
                    return GoResolveResult.fromElement(identifier);
            }
        }

        GoTypeStructAnonymousField[] anonymousFields = type.getAnonymousFields();
        for (GoTypeStructAnonymousField field : anonymousFields) {
            if (field.getFieldName().equals(unqualifiedName))
                return GoResolveResult.fromElement(field);
        }
        return GoResolveResult.NULL;
    }
}
