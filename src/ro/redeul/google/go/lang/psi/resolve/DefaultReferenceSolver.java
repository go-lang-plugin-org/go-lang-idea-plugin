package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultReferenceSolver<R extends ReferenceWithSolver<?, S, R>, S extends ReferenceSolver<R, S>>
        implements ReferenceSolver<R, S> {

    List<LookupElementBuilder> variants;
    GoPsiElement target;

    @Override
    public PsiElement resolve(R reference) {
        reference.walkSolver(self());
        return target;
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
            variants.add(target.getCompletionPresentation());
        }
    }

    @Nullable
    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) { return null; }

    @Override
    public void handleEvent(@NotNull Event event, Object associated) { }

    @Override
    public PsiElement resolveFromCache(R reference) {
        ResolvingCache.Result cachedResolvingResult = ResolveCache
                .getInstance(reference.getElement().getProject())
                .resolveWithCaching(reference, self(), true, false);

        return cachedResolvingResult != null && cachedResolvingResult.isValidResult() ? cachedResolvingResult.getElement() : null;
    }
}
