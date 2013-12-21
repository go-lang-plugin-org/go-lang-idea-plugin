package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.completion.GoCompletionContributor;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class GoPsiReference<
    GoPsi extends PsiElement,
    GoPsiRefElement extends PsiElement,
    Reference extends GoPsiReference<GoPsi, GoPsiRefElement, Reference>
    >
    implements PsiReference {

    public static AtomicInteger counts = new AtomicInteger(0);

    final GoPsi element;
    private final GoPsiRefElement reference;
    private ResolveCache.AbstractResolver<Reference, GoResolveResult> resolver;

    protected abstract Reference self();

    GoPsiReference(@NotNull GoPsi element,
                   @NotNull GoPsiRefElement reference,
                   @NotNull ResolveCache.AbstractResolver<Reference, GoResolveResult> resolver) {
        this(element, reference);
        this.resolver = resolver;
    }

    GoPsiReference(@NotNull GoPsi element, @NotNull GoPsiRefElement reference) {
        this.element = element;
        this.reference = reference;
    }

    @Override
    @NotNull
    public GoPsi getElement() {
        return element;
    }

    @NotNull
    GoPsiRefElement getReferenceElement() {
        return reference;
    }

    @Override
    public TextRange getRangeInElement() {
        return reference.getTextRange().shiftRight(-element.getTextOffset());
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

    boolean matchesVisiblePackageName(PsiElement element,
                                      String targetQualifiedName) {
        String visiblePackageName =
            element.getUserData(GoResolveStates.VisiblePackageName);

        return matchesVisiblePackageName(visiblePackageName, element,
                                         targetQualifiedName);
    }

    public boolean matchesVisiblePackageName(String currentPackageName,
                                                PsiElement element,
                                                String targetQualifiedName) {
        String elementName = element.getText();
        if (currentPackageName == null)
            currentPackageName = "";

        return matchesPackageName(currentPackageName, targetQualifiedName, elementName) || matchesPackageName(currentPackageName.toLowerCase(), targetQualifiedName, elementName);

    }

    private boolean matchesPackageName(String currentPackageName, String targetQualifiedName, String elementName) {
        if (targetQualifiedName.contains(".")) {
            elementName = currentPackageName + "." + elementName;
        }

        // this is the case when we have get variants completion.
        if (targetQualifiedName.contains(GoCompletionContributor.DUMMY_IDENTIFIER)) {
            int completionPosition = targetQualifiedName.indexOf(GoCompletionContributor.DUMMY_IDENTIFIER);

            targetQualifiedName = targetQualifiedName.substring(0, completionPosition);

            return elementName.startsWith(targetQualifiedName);
        }

        return elementName.equals(targetQualifiedName);
    }

    abstract static class Single<
        GoPsi extends PsiElement,
        Reference extends Single<GoPsi, Reference>
    > extends GoPsiReference<GoPsi, GoPsi, Reference> {

        Single(@NotNull GoPsi element, @NotNull ResolveCache.AbstractResolver<Reference, GoResolveResult> resolver) {
            super(element, element, resolver);
        }

        Single(@NotNull GoPsi element) {
            super(element, element);
        }
    }
}
