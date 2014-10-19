package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultReferenceSolver<R extends ReferenceWithSolver<?, S, R>, S extends ReferenceSolver<R, S>>
        implements ReferenceSolver<R, S> {

    List<LookupElementBuilder> variants;
    GoPsiElement target;

    @Override
    public PsiElement resolve(R reference) {
        return resolveFromCache(reference);
    }

    @Override
    public Object[] getVariants(R reference) {
        variants = new ArrayList<LookupElementBuilder>();
        reference.walkSolver(self());
        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }

    @Override
    public void addTarget(GoPsiElement target) {
        this.target = target;

        if ( variants != null ) {
            variants.add(target.getLookupPresentation());
        }
    }

    public boolean collectingVariants() {
        return variants != null;
    }

    /**
     * @return false to stop processing
     */
    @Override
    public boolean shouldContinueSolving() {
        return collectingVariants() || target == null;
    }

    @Override
    public PsiElement resolve(@NotNull R reference, boolean incompleteCode) {
        reference.walkSolver(self());
        return target;
    }

    @Nullable
    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) { return null; }

    @Override
    public void handleEvent(@NotNull Event event, Object associated) { }

    @Override
    public PsiElement resolveFromCache(R reference) {
        boolean incompleteCode = false;

        // uncomment if we want to skip cache
//        return resolve(reference, false);

        // this will go via the cache
        return ResolveCache
                .getInstance(reference.getElement().getProject())
                .resolveWithCaching(reference, self(), true, false);
    }
}
