package ro.redeul.google.go.lang.psi.resolve.references;

import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.MethodOrTypeNameSolver;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

public class CallOrConversionReference extends AbstractCallOrConversionReference<CallOrConversionReference.Solver, CallOrConversionReference> {

    public CallOrConversionReference(GoLiteralExpression expression) {
        super(expression, RESOLVER);
    }

    private static final com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver<CallOrConversionReference, ResolvingCache.Result> RESOLVER =
            ResolvingCache.<CallOrConversionReference, CallOrConversionReference.Solver>makeDefault();

    @Override
    protected CallOrConversionReference self() {
        return this;
    }

//    @NotNull
//    @Override
//    public Object[] getVariants() {
//
//        GoLiteralExpression expression = getElement();
//
//        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();
//
//        MethodOrTypeNameSolver<CallOrConversionReference> processor =
//                new Solver(variants);
//
//        GoPsiScopesUtil.treeWalkUp(
//                processor,
//                expression, expression.getContainingFile(),
//                ResolveStates.initial());
//
//        return variants.toArray();
//    }


    @Override
    public Solver newSolver() {
        return new Solver(this);
    }

    @Override
    public void walkSolver(Solver solver) {
        GoPsiScopesUtil.treeWalkUp(
                solver,
                getElement(),
                getElement().getContainingFile(),
                ResolveStates.initial());
    }

    class Solver extends MethodOrTypeNameSolver<CallOrConversionReference, Solver> {

        public Solver(CallOrConversionReference ref) {
            super(ref);
        }

//        @Override
//        protected boolean addTarget(PsiElement declaration, PsiElement child) {
//            String name = PsiUtilCore.getName(child);
//
////                        String visiblePackageName =
////                                getState().get(ResolveStates.VisiblePackageName);
//
//            String visiblePackageName = null;
//
//            if (visiblePackageName != null) {
//                name = "".equals(visiblePackageName) ?
//                        name : visiblePackageName + "." + name;
//            }
//            if (name == null) {
//                return true;
//            }
//
//            GoPsiElement goPsi = (GoPsiElement) declaration;
//            GoPsiElement goChildPsi = (GoPsiElement) child;
//            variants.add(createLookupElement(goPsi, name, goChildPsi));
//            return true;
//        }
    }
}
