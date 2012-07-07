package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

/**
 * // TODO: Explain yourself.
 */
public abstract class AbstractStructFieldsReference extends GoPsiReference<GoLiteralIdentifier> {

    public AbstractStructFieldsReference(GoLiteralIdentifier identifier) {
        super(identifier);
    }

    protected abstract GoTypeStruct resolveTypeDefinition();

    @Override
    public PsiElement resolve() {
        GoTypeStruct typeStruct = resolveTypeDefinition();

        if ( typeStruct == null )
            return null;

        for (GoTypeStructField field : typeStruct.getFields()) {
            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
                if (identifier.getUnqualifiedName().equals(getElement().getUnqualifiedName()))
                    return identifier;
            }
        }

        for (GoTypeStructAnonymousField field : typeStruct.getAnonymousFields()) {
            if (field.getFieldName().equals(getElement().getUnqualifiedName()))
                return field;
        }

        return null;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
