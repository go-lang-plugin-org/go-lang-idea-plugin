package ro.redeul.google.go.lang.psi.resolve.refs;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

public class MethodOrTypeNameReference extends ReferenceWithSolver<GoLiteralIdentifier, MethodOrTypeNameSolver, MethodOrTypeNameReference> {

//    public CallOrConversionReference(GoLiteralExpression expression) {
//        super(expression, RESOLVER);
//    }
//
//    private static final com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver<CallOrConversionReference, ResolvingCache.Result> RESOLVER =
//            ResolvingCache.<CallOrConversionReference, CallOrConversionReference.Solver>makeDefault();
//

    public MethodOrTypeNameReference(GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    protected MethodOrTypeNameReference self() { return this; }

    @Override
    public MethodOrTypeNameSolver newSolver() { return new MethodOrTypeNameSolver(this); }

    @Override
    public void walkSolver(MethodOrTypeNameSolver solver) {
        GoPsiScopesUtil.treeWalkUp(
                solver,
                getElement(),
                getElement().getContainingFile(),
                ResolveStates.initial());
    }

//    class Solver extends MethodOrTypeNameSolver<CallOrConversionReference, Solver> {
//
//        public Solver(CallOrConversionReference ref) {
//            super(ref);
//        }

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
//    }
}
