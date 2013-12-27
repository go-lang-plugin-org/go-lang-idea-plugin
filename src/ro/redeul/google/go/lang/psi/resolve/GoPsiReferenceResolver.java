package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.completion.GoCompletionContributor;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.processors.GoResolveStates.VisiblePackageName;

public abstract class GoPsiReferenceResolver<Reference extends PsiReference>
    extends GoElementVisitor
    implements PsiScopeProcessor {

    private PsiElement declaration;
    private PsiElement childDeclaration;
    private final Reference reference;
    private ResolveState state;

    GoPsiReferenceResolver(Reference reference) {
        this.reference = reference;
    }

    public boolean execute(@NotNull PsiElement element, ResolveState state) {

        if (element instanceof GoPsiElement) {
            this.state = state;
            ((GoPsiElement) element).accept(this);
            this.state = null;
        }

        return declaration == null;
    }

    boolean checkReference(PsiElement element) {
        if ( element == null )
            return false;

        try {
            element.putUserData(VisiblePackageName,
                                getState().get(VisiblePackageName));
            return getReference().isReferenceTo(element);

        } finally {
            element.putUserData(VisiblePackageName, null);
        }
    }

    Reference getReference() {
        return reference;
    }

    /**
     * @param declaration add new declaration
     * @return false if we want to stop processing
     */
    final boolean addDeclaration(PsiElement declaration) {
        return addDeclaration(declaration, declaration);
    }

    /**
     * @param declaration add new declaration
     * @return false if we want to stop processing
     */
    protected boolean addDeclaration(PsiElement declaration, PsiElement child) {
        this.declaration = declaration;
        this.childDeclaration = child;
        return true;
    }

    public ResolveState getState() {
        return state;
    }

    public PsiElement getDeclaration() {
        return declaration;
    }

    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(Event event, @Nullable Object associated) {
    }

    public PsiElement getChildDeclaration() {
        return childDeclaration;
    }

    protected void checkIdentifiers(PsiElement... identifiers) {
        String refName = getReference().getElement().getText();
        String currentPackageName = getState().get(GoResolveStates.VisiblePackageName);
        if (currentPackageName == null) {
            currentPackageName = "";
        }
        boolean isOriginalPackage = getState().get(GoResolveStates.IsOriginalPackage);
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
            if (isOriginalPackage || GoNamesUtil.isExportedName(name)) {
                if (refName.contains(".")) {
                    name = currentPackageName + "." + name;
                }
                if (incomplete && name.toLowerCase().startsWith(refName.toLowerCase())) {
                    addDeclaration(id);
                    return;
                } else if (refName.equals(name)) {
                    addDeclaration(id);
                    return;
                }
            }
        }
    }
}
