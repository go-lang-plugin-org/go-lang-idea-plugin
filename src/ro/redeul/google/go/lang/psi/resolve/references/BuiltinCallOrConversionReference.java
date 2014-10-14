package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.MethodOrTypeNameSolver;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class BuiltinCallOrConversionReference extends AbstractCallOrConversionReference<BuiltinCallOrConversionReference.Solver, BuiltinCallOrConversionReference> {

    public static final ElementPattern<GoLiteralExpression> MATCHER =
                psiElement(GoLiteralExpression.class)
                    .withParent(psiElement(GoBuiltinCallExpression.class))
                    .atStartOf(psiElement(GoBuiltinCallExpression.class));

    public BuiltinCallOrConversionReference(GoLiteralExpression identifier) {
        super(identifier, RESOLVER);
    }

    @Override
    protected BuiltinCallOrConversionReference self() {
        return this;
    }

    private static final com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver<BuiltinCallOrConversionReference, ResolvingCache.Result> RESOLVER =
            ResolvingCache.<BuiltinCallOrConversionReference, BuiltinCallOrConversionReference.Solver>makeDefault();

//    @Override
//    public PsiElement resolve() {
//        return ResolveCache.getInstance(getElement().getProject())
//                    .resolveWithCaching(this, RESOLVER, false, false);
//    }


//    @NotNull
//    @Override
//    public Object[] getVariants() {
//
//        PsiElement element = getElement();
//
//        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();
//
//        MethodOrTypeNameSolver processor = new Solver();
//
//        GoNamesCache namesCache = GoNamesCache.getInstance(element.getProject());
//
//        // get the file included in the imported package name
//        Collection<GoFile> files = namesCache.getBuiltinPackageFiles();
//
//        for (GoFile file : files) {
//            if (!file.processDeclarations(processor, ResolveStates.builtins(), null, element))  {
//                break;
//            }
//        }
//
//        return variants.toArray(new LookupElementBuilder[variants.size()]);
//    }

    @Override
    public boolean isSoft() {
        return false;
    }

    @Override
    public Solver newSolver() {
        return new Solver(this);
    }

    @Override
    public void walkSolver(Solver solver) {
        GoPackage builtIn = GoPackages.getInstance(element.getProject()).getBuiltinPackage();

        GoPsiScopesUtil.packageWalk(builtIn, solver, element, ResolveStates.builtins());
    }

    class Solver extends MethodOrTypeNameSolver<BuiltinCallOrConversionReference, Solver> {

        public Solver(BuiltinCallOrConversionReference ref) {
            super(ref);
        }
//        @Override
//        protected boolean addTarget(PsiElement declaration, PsiElement child) {
//
//            String name = PsiUtilCore.getName(child);
//
//            GoPsiElement goPsi = (GoPsiElement) declaration;
//            GoPsiElement goChildPsi = (GoPsiElement) child;
//            variants.add(createLookupElement(goPsi, name, goChildPsi).withTypeText("builtin", true));
//            return true;
//        }
    }
}
