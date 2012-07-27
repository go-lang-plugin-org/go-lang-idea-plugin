package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.concurrent.atomic.AtomicInteger;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;

public abstract class GoPsiReference<
    GoPsi extends PsiElement,
    Reference extends GoPsiReference<GoPsi, Reference>
    >
    implements PsiReference {

    public static AtomicInteger counts = new AtomicInteger(0);

    GoPsi element;
    ResolveCache.AbstractResolver<Reference, GoResolveResult> resolver;

    protected abstract Reference self();

    protected GoPsiReference(@NotNull GoPsi element, @NotNull ResolveCache.AbstractResolver<Reference, GoResolveResult> resolver) {
        this.element = element;
        this.resolver = resolver;
    }

    protected GoPsiReference(@NotNull GoPsi element) {
        this.element = element;
    }

    @Override
    @NotNull
    public GoPsi getElement() {
        return element;
    }

    @Override
    public TextRange getRangeInElement() {
        if (element == null)
            return new TextRange(0, 0);

        return new TextRange(0, element.getTextLength());
    }

    @Override
    public PsiElement resolve() {
        if (resolver != null) {
            GoResolveResult result = ResolveCache
                .getInstance(getElement().getProject())
                .resolveWithCaching(self(), resolver, true, false);

            return result != null && result.isValidResult()
                ? result.getElement()
                : null;
        }

        return null;
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
                                                String targetQualifiedName) {
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
            targetQualifiedName = targetQualifiedName.replace(
                "IntellijIdeaRulezzz", "");
            return elementName.startsWith(targetQualifiedName);
        }

        return elementName.equals(targetQualifiedName);
    }
}
