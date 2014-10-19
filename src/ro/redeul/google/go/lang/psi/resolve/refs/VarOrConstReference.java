package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class VarOrConstReference
        extends ReferenceWithSolver<GoLiteralIdentifier, VarOrConstSolver, VarOrConstReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoLiteralExpression.class)
                    );

    public VarOrConstReference(GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    protected VarOrConstReference self() {
        return this;
    }

    @Override
    public VarOrConstSolver newSolver() {
        return new VarOrConstSolver(self());
    }

    @Override
    public void walkSolver(VarOrConstSolver solver) {
        GoPsiScopesUtil.treeWalkUp(
                solver,
                getElement().getParent().getParent(),
                getElement().getContainingFile(),
                ResolveStates.initial());
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return getElement().getManager().areElementsEquivalent(resolve(), element);
    }

    //    @NotNull
//    @Override
//    public Object[] getVariants() {
//
//        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();
//
//        VarOrConstSolver processor = new VarOrConstSolver(this) {
//            @Override
//            protected boolean addTarget(PsiElement declaration, PsiElement childDeclaration) {
//                String name = PsiUtilCore.getName(declaration);
//
//                String visiblePackageName = "";
////                        getState().get(ResolveStates.VisiblePackageName);
//
//                if (visiblePackageName != null) {
//                    name = "".equals(visiblePackageName) ?
//                            name : visiblePackageName + "." + name;
//                }
//                if (name == null) {
//                    return true;
//                }
//
//                GoPsiElement goPsi = (GoPsiElement) declaration;
//                GoPsiElement goChildPsi = (GoPsiElement) childDeclaration;
//                variants.add(createLookupElement(goPsi, name, goChildPsi));
//                return true;
//            }
//        };
//
//        GoPsiScopesUtil.treeWalkUp(
//                processor,
//                getElement().getParent().getParent(),
//                getElement().getContainingFile(),
//                ResolveStates.initial());
//
//        return variants.toArray();
//    }
//
}
