package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.GoReturnVariableResolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class GoReturnVariableReference extends GoPsiReference.Single<GoLiteralIdentifier, GoReturnVariableReference> {

    public static ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(psiElement(GoShortVarDeclaration.class));

    static ResolveCache.AbstractResolver<GoReturnVariableReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<GoReturnVariableReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(GoReturnVariableReference reference, boolean incompleteCode) {

                PsiElement element = reference.getElement().getParent();
                while ( element != null && !(element.getParent() instanceof GoFile)) {
                    element = element.getParent();
                }

                if (element == null)
                    return GoResolveResult.NULL;

                GoReturnVariableResolver returnVarResolver =
                    new GoReturnVariableResolver(reference);

                PsiScopesUtil.treeWalkUp(
                    returnVarResolver,
                    reference.getElement().getParent(),
                    element,
                    GoResolveStates.initial());

                PsiElement declaration = returnVarResolver.getDeclaration();
                return declaration != null ? new GoResolveResult(declaration) : GoResolveResult.NULL;
            }
        };

    public GoReturnVariableReference(@NotNull GoLiteralIdentifier element) {
        super(element, RESOLVER);
    }

    @Override
    protected GoReturnVariableReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (!(element instanceof GoLiteralIdentifier))
            return false;

        GoLiteralIdentifier identifier = (GoLiteralIdentifier) element;

        String myName = getElement().getName();
        return myName != null && myName.equals(identifier.getName());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSoft() {
        return true;
    }
}
