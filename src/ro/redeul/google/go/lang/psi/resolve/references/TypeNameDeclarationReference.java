package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;

public class TypeNameDeclarationReference
        extends GoPsiReference.Single<GoTypeNameDeclaration, TypeNameDeclarationReference> {

    public TypeNameDeclarationReference(@NotNull GoTypeNameDeclaration element) {
        super(element);
    }

    @Override
    protected TypeNameDeclarationReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getText();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return element instanceof GoTypeSpec && matchesVisiblePackageName(element, getElement().getName());
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
