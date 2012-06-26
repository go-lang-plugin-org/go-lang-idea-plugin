package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;

public abstract class GoPsiReference<GoPsi extends PsiNamedElement>
    implements PsiReference {

    GoPsi element;

    protected GoPsiReference(GoPsi element) {
        this.element = element;
    }

    @Override
    public GoPsi getElement() {
        return element;
    }

    @Override
    public TextRange getRangeInElement() {
        return new TextRange(0, element.getTextLength());
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return element.getText();
    }

    @Override
    public PsiElement handleElementRename(String newElementName)
        throws IncorrectOperationException {
        throw new IncorrectOperationException(
            GoBundle.message("error.not.implemented"));
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element)
        throws IncorrectOperationException {
        throw new IncorrectOperationException(
            GoBundle.message("error.not.implemented"));
    }

    protected boolean matchesVisiblePackageName(PsiElement element,
                                                String targetQualifiedName)
    {
        String visiblePackageName =
            element.getUserData(GoResolveStates.VisiblePackageName);

        return matchesVisiblePackageName(visiblePackageName, element,
                                         targetQualifiedName);
    }

    protected boolean matchesVisiblePackageName(String currentPackageName,
                                                PsiElement element,
                                                String targetQualifiedName) {
        String visiblePackageName =
            element.getUserData(GoResolveStates.VisiblePackageName);

        String elementName = element.getText();
        if (currentPackageName == null)
            currentPackageName = "";

        if (targetQualifiedName.contains(".")) {
            elementName = currentPackageName + "." + elementName;
        }

        // this is the case when we have get variants completion.
        if (targetQualifiedName.endsWith("IntellijIdeaRulezzz")) {
            targetQualifiedName = targetQualifiedName.replace("IntellijIdeaRulezzz", "");
            return elementName.startsWith(targetQualifiedName);
        }

        return elementName.equals(targetQualifiedName);
    }
}
