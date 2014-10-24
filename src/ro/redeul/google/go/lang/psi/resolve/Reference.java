package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;

public abstract class Reference<
        E extends GoPsiElement,
        Self extends Reference<E, Self>> implements PsiReference {

    E element;

    protected Reference(E element) {
        this.element = element;
    }

    @Override
    public PsiElement getElement() {
        return element;
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    protected abstract Self self();

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getText();
    }

    @Override
    public TextRange getRangeInElement() {
        PsiElement navigationElement = getElement().getNavigationElement();
        return navigationElement.getTextRange().shiftRight(-getElement().getTextOffset());
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return getElement().getManager().areElementsEquivalent(resolve(), element);
    }

    public boolean canSee(GoPsiElement element, String name) {
        GoPackage elementPackage = GoPackages.getPackageFor(element);
        GoPackage referencePackage = GoPackages.getPackageFor(getElement());

        return elementPackage == referencePackage || GoNamesUtil.isExported(name);
    }

    public String name() {
        PsiElement element = this.element;

        return (element != null && element instanceof PsiNamedElement)
                ? ((PsiNamedElement) element).getName()
                : element != null
                ? element.getText() : null;
    }
}
