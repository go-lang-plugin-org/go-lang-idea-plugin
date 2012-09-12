package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

public class TypeSpecReference
        extends GoPsiReference.Single<GoTypeSpec, TypeSpecReference> {

    public TypeSpecReference(@NotNull GoTypeSpec element) {
        super(element);
    }

    @Override
    protected TypeSpecReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getText();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof GoTypeSpec) {
            return matchesVisiblePackageName(element,
                    getElement().getTypeNameDeclaration().getName());
        }
        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    @Override
    public PsiElement resolve() {
        return getElement();
    }
}
