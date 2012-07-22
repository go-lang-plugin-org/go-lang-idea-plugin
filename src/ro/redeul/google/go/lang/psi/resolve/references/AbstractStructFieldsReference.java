package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

public abstract class AbstractStructFieldsReference
        extends GoPsiReference<GoLiteralIdentifier, AbstractStructFieldsReference> {

    public AbstractStructFieldsReference(GoLiteralIdentifier identifier) {
        super(identifier, RESOLVER);
    }

    protected abstract GoPsiTypeStruct resolveTypeDefinition();

    private static final ResolveCache.AbstractResolver<AbstractStructFieldsReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<AbstractStructFieldsReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(AbstractStructFieldsReference psiReference, boolean incompleteCode) {

                GoPsiTypeStruct typeStruct = psiReference.resolveTypeDefinition();

                if ( typeStruct == null )
                    return null;

                GoLiteralIdentifier element = psiReference.getElement();

                for (GoTypeStructField field : typeStruct.getFields()) {
                    for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                        if (identifier.getUnqualifiedName().equals(element.getUnqualifiedName()))
                            return new GoResolveResult(identifier);
                    }
                }

                for (GoTypeStructAnonymousField field : typeStruct.getAnonymousFields()) {
                    if (field.getFieldName().equals(element.getUnqualifiedName()))
                        return new GoResolveResult(field);
                }

                return GoResolveResult.NULL;
            }
        };

//    @Override
//    public PsiElement resolve() {
//        return ResolveCache.getInstance(getElement().getProject())
//                           .resolveWithCaching(this, RESOLVER, false, false);
//
//    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        GoPsiTypeStruct typeStruct = resolveTypeDefinition();

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
