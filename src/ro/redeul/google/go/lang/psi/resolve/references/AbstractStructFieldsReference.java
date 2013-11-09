package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

abstract class AbstractStructFieldsReference
    <
        T extends GoPsiElement,
        Ref extends AbstractStructFieldsReference<T, Ref>
        >
    extends GoPsiReference<T, GoLiteralIdentifier, Ref> {

    AbstractStructFieldsReference(T element,
                                  GoLiteralIdentifier name,
                                  ResolveCache.AbstractResolver<Ref, GoResolveResult> RESOLVER) {
        super(element, name, RESOLVER);
    }

    protected abstract GoTypeStruct resolveTypeDefinition();

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return getElement().getManager().areElementsEquivalent(resolve(), element);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        GoTypeStruct typeStruct = resolveTypeDefinition();

        if (typeStruct == null || typeStruct.getPsiType() == null)
            return LookupElementBuilder.EMPTY_ARRAY;

        List<LookupElementBuilder> variants = new ArrayList<>();

        GoPsiTypeStruct psiType = typeStruct.getPsiType();
        for (GoTypeStructField field : psiType.getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                variants.add(field.getCompletionPresentation(identifier));
            }
        }

        for (GoTypeStructAnonymousField field : psiType.getAnonymousFields()) {
            variants.add(field.getCompletionPresentation());
        }

        GoTypeStructPromotedFields promotedFields = psiType.getPromotedFields();
        for (GoLiteralIdentifier identifier : promotedFields.getNamedFields()) {
            GoTypeStructField field = findParentOfType(identifier, GoTypeStructField.class);
            if (field != null) {
                variants.add(field.getCompletionPresentation(identifier));
            }
        }

        for (GoTypeStructAnonymousField field : promotedFields.getAnonymousFields()) {
            variants.add(field.getCompletionPresentation());
        }
        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
