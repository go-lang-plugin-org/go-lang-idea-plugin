package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

public abstract class AbstractStructFieldsReference
        extends GoPsiReference<GoLiteralIdentifier, AbstractStructFieldsReference> {

    public AbstractStructFieldsReference(GoLiteralIdentifier identifier) {
        super(identifier, RESOLVER);
    }

    protected abstract GoTypeStruct resolveTypeDefinition();

    private static final ResolveCache.AbstractResolver<AbstractStructFieldsReference, PsiElement> RESOLVER =
        new ResolveCache.AbstractResolver<AbstractStructFieldsReference, PsiElement>() {
            @Override
            public PsiElement resolve(AbstractStructFieldsReference psiReference, boolean incompleteCode) {
                GoTypeStruct typeStruct = psiReference.resolveTypeDefinition();

                if ( typeStruct == null )
                    return null;

                GoLiteralIdentifier element = psiReference.getElement();

                for (GoTypeStructField field : typeStruct.getFields()) {
                    for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                        if (identifier.getUnqualifiedName().equals(element.getUnqualifiedName()))
                            return identifier;
                    }
                }

                for (GoTypeStructAnonymousField field : typeStruct.getAnonymousFields()) {
                    if (field.getFieldName().equals(element.getUnqualifiedName()))
                        return field;
                }

                return null;
            }
        };

    @Override
    public PsiElement resolve() {
        return ResolveCache.getInstance(getElement().getProject())
                           .resolveWithCaching(this, RESOLVER, false, false);

    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        GoTypeStruct typeStruct = resolveTypeDefinition();

        List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        for (GoTypeStructField field : typeStruct.getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                variants.add(field.getCompletionPresentation(identifier));
            }
        }

        for (GoTypeStructAnonymousField field : typeStruct.getAnonymousFields()) {
            variants.add(field.getCompletionPresentation());
        }

        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
