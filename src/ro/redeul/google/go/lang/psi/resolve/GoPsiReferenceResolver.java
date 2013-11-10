package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
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

    public boolean execute(PsiElement element, ResolveState state) {

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
    public <T> T getHint(Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(Event event,
                            @Nullable Object associated) {
    }

    public PsiElement getChildDeclaration() {
        return childDeclaration;
    }
}
