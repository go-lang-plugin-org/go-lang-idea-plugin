package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.resolve.VarOrConstSolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

public class VarOrConstReference
        extends Reference.Single<GoLiteralIdentifier, VarOrConstSolver, VarOrConstReference> {

    GoPackage targetPackage;

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoLiteralExpression.class)
                    );

    private static final com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver<VarOrConstReference, ResolvingCache.Result> RESOLVER =
            ResolvingCache.<VarOrConstReference, VarOrConstSolver>makeDefault();

    public VarOrConstReference(GoLiteralIdentifier element) {
        this(element, null);
    }

    public VarOrConstReference(GoLiteralIdentifier element, GoPackage targetPackage) {
        super(element, RESOLVER);
        this.targetPackage = targetPackage;
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

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
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
    @Override
    public boolean isSoft() {
        return false;
    }
}
