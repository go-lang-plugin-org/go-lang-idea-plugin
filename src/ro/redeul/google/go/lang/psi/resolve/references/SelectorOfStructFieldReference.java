package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.typing.GoTypes.resolveToStruct;

public class SelectorOfStructFieldReference
    extends AbstractStructFieldsReference<GoSelectorExpression, SelectorOfStructFieldReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoSelectorExpression.class)
                    );

    public SelectorOfStructFieldReference(GoSelectorExpression expression) {
        super(expression, expression.getIdentifier(), RESOLVER);
    }

    private static final ResolveCache.AbstractResolver<SelectorOfStructFieldReference, ResolvingCache.Result> RESOLVER =
        new ResolveCache.AbstractResolver<SelectorOfStructFieldReference, ResolvingCache.Result>() {
            @Override
            public ResolvingCache.Result resolve(@NotNull SelectorOfStructFieldReference psiReference, boolean incompleteCode) {

                GoTypeStruct typeStruct = psiReference.resolveTypeDefinition();

                if ( typeStruct == null || typeStruct.getPsiType() == null)
                    return null;

                GoLiteralIdentifier element = psiReference.getReferenceElement();
                GoPsiTypeStruct type = typeStruct.getPsiType();
                String unqualifiedName = element.getUnqualifiedName();
                ResolvingCache.Result result = findDirectFieldOfName(type, unqualifiedName);
                if (result == ResolvingCache.Result.NULL) {
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

    private static ResolvingCache.Result findPromotedFieldOfName(GoPsiTypeStruct type, String unqualifiedName) {
        if (type == null || StringUtil.isEmpty(unqualifiedName)) {
            return ResolvingCache.Result.NULL;
        }

        GoTypeStructPromotedFields promotedFields = type.getPromotedFields();
        for (GoLiteralIdentifier identifier : promotedFields.getNamedFields()) {
            if (unqualifiedName.equals(identifier.getUnqualifiedName())) {
                return ResolvingCache.Result.fromElement(identifier);
            }
        }

        for (GoTypeStructAnonymousField field : promotedFields.getAnonymousFields()) {
            if (unqualifiedName.equals(field.getFieldName())) {
                return ResolvingCache.Result.fromElement(field);
            }
        }
        return ResolvingCache.Result.NULL;
    }

    private static ResolvingCache.Result findDirectFieldOfName(GoPsiTypeStruct type, String unqualifiedName) {
        if (type == null) {
            return ResolvingCache.Result.NULL;
        }

        for (GoTypeStructField field : type.getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                if (identifier.getUnqualifiedName().equals(unqualifiedName))
                    return ResolvingCache.Result.fromElement(identifier);
            }
        }

        GoTypeStructAnonymousField[] anonymousFields = type.getAnonymousFields();
        for (GoTypeStructAnonymousField field : anonymousFields) {
            if (field.getFieldName().equals(unqualifiedName))
                return ResolvingCache.Result.fromElement(field);
        }
        return ResolvingCache.Result.NULL;
    }
}
