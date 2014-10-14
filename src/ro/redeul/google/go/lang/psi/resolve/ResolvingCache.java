package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResolvingCache {

    public interface SolverBasedResolver<R extends ReferenceWithSolver<?, S, R>, S extends ReferenceSolver<R, S>> extends ResolveCache.AbstractResolver<R, Result> {
    }

    public static <R extends ReferenceWithSolver<?, S, R>, S extends ReferenceSolver<R, S>> SolverBasedResolver<R, S> makeDefault() {
        return new SolverBasedResolver<R, S>() {
            @Override
            public Result resolve(@NotNull R reference, boolean incompleteCode) {

                S solver = reference.newSolver();

                ResolvingCache.Result result = ResolveCache
                        .getInstance(reference.getElement().getProject())
                        .resolveWithCaching(reference.self(), reference.newSolver(), true, false);

//                reference.newSolver().resolve();
//
//                reference.walkSolver(processor);

                return Result.fromElement(solver.getTarget());
            }
        };
    }
//
//    public static <R extends ReferenceWithSolver<?, S, R>, S extends ReferenceSolver<R, S>> S makeSolver() {
//        return new ReferenceSolver<R, S>() {
//
//            Resolver<R> resolver = null;
//
//            @Override
//            public PsiElement resolve(R reference) {
//                if (resolver != null) {
//                    ResolvingCache.Result result = ResolveCache
//                            .getInstance(reference.getElement().getProject())
//                            .resolveWithCaching(reference.self(), resolver, true, false);
//
//                    return result != null && result.isValidResult()
//                            ? result.getElement()
//                            : null;
//                }
//
//                return null;
//            }
//
//            @Override
//            public Object[] getVariants(R reference) {
//                Solver resolver = newSolver();
//                resolver.setCollectVariants(true);
//                walkSolver(resolver);
//                return resolver.getVariants();
//            }
//        }
//    }

    public static class Result implements ResolveResult {

        public static final Result NULL = new Result(null);

        private final PsiElement element;

        public static Result fromElement(@Nullable PsiElement element) {
            if ( element != null && element.isValid())
                return new Result(element);
            else
                return NULL;
        }

        public Result(PsiElement element) {
            this.element = element;
        }

        @Override
        public PsiElement getElement() {
            return element;
        }

        @Override
        public boolean isValidResult() {
            return element != null;
        }

        @Override
        public String toString() {
            StringBuilder elementTree = new StringBuilder();

            elementTree.append(element == null ? element : "" + element.getText().replaceAll("[\n]", "\\n") + " : " + element.isValid() + " ");

            PsiElement elem = element;
            while (elem != null && !elem.isValid() ) {
                elementTree.append("[ ").append(elem.getText().replaceAll("[\n]", "\\n")).append(" ]");
                elementTree.append(" -> ");
                elem = elem.getParent();
            }

            if (elem == null)
                elementTree.append("[ ]");

            return "GoResolveResult{element=" + elementTree +'}';
        }
    }
}
