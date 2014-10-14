package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.completion.GoCompletionContributor;
import ro.redeul.google.go.lang.psi.resolve.RefSolver;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;

public abstract class Reference<
    ParentElement extends PsiElement,
    Element extends PsiElement,
    Solver extends RefSolver<Ref, Solver>,
    Ref extends Reference<ParentElement, Element, Solver, Ref>
    > implements PsiReference {

    final ParentElement element;
    private final Element reference;
    private ResolveCache.AbstractResolver<Ref, ResolvingCache.Result> resolver;

    protected abstract Ref self();

    Reference(@NotNull ParentElement element,
              @NotNull Element reference,
              @NotNull ResolveCache.AbstractResolver<Ref, ResolvingCache.Result> resolver) {
        this(element, reference);
        this.resolver = resolver;
    }

    Reference(@NotNull ParentElement element, @NotNull Element reference) {
        this.element = element;
        this.reference = reference;
    }

    @Override
    @NotNull
    public ParentElement getElement() {
        return element;
    }

    @NotNull
    public Element getReferenceElement() {
        return reference;
    }

    @Override
    public TextRange getRangeInElement() {
        return reference.getTextRange().shiftRight(-element.getTextOffset());
    }

    @Override
    public PsiElement resolve() {
        if (resolver != null) {
            ResolvingCache.Result result = ResolveCache
                .getInstance(getElement().getProject())
                .resolveWithCaching(self(), resolver, true, false);

            return result != null && result.isValidResult()
                ? result.getElement()
                : null;
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Solver resolver = newSolver();
        resolver.setCollectVariants(true);
        walkSolver(resolver);
        return resolver.getVariants();
    }

    public abstract Solver newSolver();

    public abstract void walkSolver(RefSolver<?, ?> processor);

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
//        String visiblePackageName = element.getUserData(ResolveStates.VisiblePackageName);

//        return matchesVisiblePackageName(visiblePackageName, element,
//                                         targetQualifiedName);
        return false;
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
        if (!currentPackageName.equals("")) {
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
        Solver extends RefSolver<Ref, Solver>,
        Ref extends Single<GoPsi, Solver, Ref>
    > extends Reference<GoPsi, GoPsi, Solver, Ref> {

        Single(@NotNull GoPsi element, @NotNull ResolveCache.AbstractResolver<Ref, ResolvingCache.Result> resolver) {
            super(element, element, resolver);
        }

        Single(@NotNull GoPsi element) {
            super(element, element);
        }
    }
}
