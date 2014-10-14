package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.PresentationUtil;
import ro.redeul.google.go.lang.completion.GoCompletionContributor;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.Reference;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;


public abstract class RefSolver<
        Ref extends Reference<? extends PsiElement, ? extends PsiElement, Solver, Ref>,
        Solver extends RefSolver<Ref, Solver>
        >
        extends GoElementVisitor implements PsiScopeProcessor {

    private PsiElement targetElementParent;
    private PsiElement targetElement;

    private final Ref ref;

    private ResolveState state;

    private List<LookupElementBuilder> variants;

    RefSolver(Ref ref) {
        this.ref = ref;
        setCollectVariants(false);
    }

    public void setCollectVariants(boolean collectorMode) {
        if (collectorMode)
            variants = new ArrayList<LookupElementBuilder>();
        else
            variants = null;
    }

    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {

        if (element instanceof GoPsiElement) {
            this.state = state;
            ((GoPsiElement) element).accept(this);
            this.state = null;
        }

        return targetElementParent == null;
    }

    boolean checkReference(PsiElement element) {
        if (element == null)
            return false;

        try {
//            element.putUserData(VisiblePackageName, getState().get(VisiblePackageName));
            return getReference().isReferenceTo(element);

        } finally {
//            element.putUserData(VisiblePackageName, null);
        }
    }

    Ref getReference() {
        return ref;
    }

    /**
     * @param declaration add new targetElementParent
     * @return false if we want to stop processing
     */
    final boolean addTarget(PsiNamedElement declaration) {
        return addTarget(declaration, declaration);
    }

    /**
     * @param targetElementParent add new targetElementParent
     * @return false if we want to stop processing
     */
    protected boolean addTarget(PsiNamedElement targetElementParent, PsiElement targetElement) {
        this.targetElementParent = targetElementParent;
        this.targetElement = targetElement;

        if ( variants != null ) {
            variants.add(PresentationUtil.elementToLookupElementBuilder(targetElementParent));
        }

        return variants != null;
    }

    public ResolveState getState() {
        return state;
    }

    public PsiElement getDeclaration() {
        return targetElementParent;
    }

    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(Event event, @Nullable Object associated) {
    }

    public PsiElement getChildDeclaration() {
        return targetElement;
    }

    protected void checkIdentifiers(String name, GoLiteralIdentifier ... identifiers) {
        for (GoLiteralIdentifier identifier : identifiers) {
            if (matchNames(identifier.getUnqualifiedName(), name)) {
                addTarget(identifier);
            }
        }
    }

  /*  protected void checkIdentifiers(PsiElement... identifiers) {
        String refName = getReference().getElement().getText();
//        String currentPackageName = getState().get(GoResolveStates.VisiblePackageName);
//        if (currentPackageName == null) {
//            currentPackageName = "";
//        }
        boolean isOriginalPackage = ResolveStates.get(getState(), ResolveStates.Key.IsOriginalPackage);
        boolean incomplete = refName.contains(GoCompletionContributor.DUMMY_IDENTIFIER);
        if (incomplete) {
            int completionPosition = refName.indexOf(GoCompletionContributor.DUMMY_IDENTIFIER);
            refName = refName.substring(0, completionPosition);
        }
        for (PsiElement id : identifiers) {
            if (id == null) {
                continue;
            }
            String name = id.getText();
            if (isOriginalPackage || GoNamesUtil.isExported(name)) {
                if (refName.contains(".")) {
//                    name = currentPackageName + "." + name;
                }
                if (incomplete && name.toLowerCase().startsWith(refName.toLowerCase())) {
                    addTarget(id);
                    return;
                } else if (refName.equals(name)) {
                    addTarget(id);
                    return;
                }
            }
        }
    }*/

    protected String getReferenceName() {
        return getReference().getReferenceElement().getText();
    }

    protected boolean matchNames(String potentialTarget, String name) {
        boolean incomplete = name.contains(GoCompletionContributor.DUMMY_IDENTIFIER);
        if (incomplete) {
            int completionPosition = name.indexOf(GoCompletionContributor.DUMMY_IDENTIFIER);
            name = name.substring(0, completionPosition);
        }

        return incomplete ? potentialTarget.toLowerCase().startsWith(name.toLowerCase()) : potentialTarget.equals(name);
    }

    public LookupElementBuilder[] getVariants() {
        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }
}
