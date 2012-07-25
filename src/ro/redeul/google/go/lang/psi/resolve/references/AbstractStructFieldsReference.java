package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

public abstract class AbstractStructFieldsReference
        extends GoPsiReference<GoLiteralIdentifier, AbstractStructFieldsReference> {

    public AbstractStructFieldsReference(GoLiteralIdentifier identifier) {
        super(identifier, RESOLVER);
    }

    protected abstract GoTypeStruct resolveTypeDefinition();

    private static final ResolveCache.AbstractResolver<AbstractStructFieldsReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<AbstractStructFieldsReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(AbstractStructFieldsReference psiReference, boolean incompleteCode) {

                GoTypeStruct typeStruct = psiReference.resolveTypeDefinition();

                if ( typeStruct == null || typeStruct.getPsiType() == null)
                    return null;

                GoLiteralIdentifier element = psiReference.getElement();


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
    public PsiElement resolve() {
        GoResolveResult result =
            ResolveCache.getInstance(
                getElement().getProject())
                        .resolveWithCaching(this, RESOLVER, false, false);

        return result != null ? result.getElement() : null;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        GoTypeStruct typeStruct = resolveTypeDefinition();

        if ( typeStruct == null || typeStruct.getPsiType() == null)
            return LookupElementBuilder.EMPTY_ARRAY;

        List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        for (GoTypeStructField field : typeStruct.getPsiType().getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                variants.add(field.getCompletionPresentation(identifier));
            }
        }

        for (GoTypeStructAnonymousField field : typeStruct.getPsiType().getAnonymousFields()) {
            variants.add(field.getCompletionPresentation());
        }

        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
