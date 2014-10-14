package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.PsiScopeProcessor;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
* Created by mihai on 10/14/14.
*/
interface ReferenceSolver<R extends ReferenceWithSolver<?, S, R>, S extends ReferenceSolver<R, S>>
        extends PsiScopeProcessor, ResolveCache.AbstractResolver<R, ResolvingCache.Result> {

    PsiElement resolveFromCache(R reference);

    public PsiElement resolve(R reference);

    public Object[] getVariants(R reference);

    public void addTarget(GoPsiElement targetPsi);

    S self();
}

