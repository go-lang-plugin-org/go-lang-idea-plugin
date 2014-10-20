package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

public class TypeNameReference
        extends ReferenceWithSolver<GoPsiTypeName, TypeNameSolver, TypeNameReference> {

    public static final ElementPattern<GoPsiTypeName> MATCHER =
            psiElement(GoPsiTypeName.class);

    @SuppressWarnings("unchecked")
    private static final ElementPattern<GoPsiTypeName> TYPE_IN_METHOD_RECEIVER =
            psiElement(GoPsiTypeName.class).withParent(
                    or(
                            psiElement(GoMethodReceiver.class),
                            psiElement(GoPsiTypePointer.class).withParent(psiElement(GoMethodReceiver.class))
                    )
            );

    private final GoPackage goPackage;

//    private static final com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver<TypeNameReference, ResolvingCache.Result> RESOLVER =
//            ResolvingCache.<TypeNameReference, TypeNameSolver>makeDefault();
//
//    public TypeNameReference(GoPsiTypeName element) {
//        super(element, RESOLVER);
//    }

    public TypeNameReference(GoPsiTypeName element) {
        this(element, null);
    }

    public TypeNameReference(GoPsiTypeName element, GoPackage goPackage) {
        super(element);
        this.goPackage = goPackage;
    }

    @Override
    protected TypeNameReference self() { return this; }

    @Override
    public TypeNameSolver newSolver() {
        return new TypeNameSolver(self());
    }


    @Override
    public void walkSolver(TypeNameSolver solver) {
        if ( goPackage == null)
            GoPsiScopesUtil.treeWalkUp(
                    solver,
                    getElement(),
                    getElement().getContainingFile(),
                    ResolveStates.initial());
        else
            GoPsiScopesUtil.walkPackage(solver, getElement(), goPackage);
    }

    @Override
    public TextRange getRangeInElement() {
        return super.getRangeInElement();
    }

    //    @NotNull
//    @Override
//    public Object[] getVariants() {
//
//        final List<LookupElement> variants = new ArrayList<LookupElement>();
//
//        // According to the spec, method receiver type "T" could not be an interface or a pointer.
//        final boolean rejectInterfaceAndPointer = TYPE_IN_METHOD_RECEIVER.accepts(getElement());
//        Collections.addAll(variants, getImportedPackagesNames(getElement().getContainingFile()));
//
//        TypeNameSolver processor =
//                new TypeNameSolver(this) {
//                    @Override
//                    protected boolean addTarget(PsiElement declaration, PsiElement childDeclaration) {
//                        if (rejectInterfaceAndPointer && isInterfaceOrPointer(declaration)) {
//                            return true;
//                        }
//
//                        String name = PsiUtilCore.getName(declaration);
//
////                        String visiblePackageName =
////                                getState().get(ResolveStates.VisiblePackageName);
//
//                        String visiblePackageName = null;
//
//                        if (visiblePackageName != null) {
//                            name = "".equals(visiblePackageName) ?
//                                    name : visiblePackageName + "." + name;
//                        }
//                        if (name == null) {
//                            return true;
//                        }
//
//                        GoPsiElement goDeclaration = (GoPsiElement) declaration;
//                        GoPsiElement goChildDeclaration = (GoPsiElement) childDeclaration;
//
//                        variants.add(
//                                createLookupElement(
//                                        goDeclaration,
//                                        name,
//                                        goChildDeclaration));
//                        return true;
//
//                    }
//                };
//
//        GoPsiScopesUtil.treeWalkUp(
//                processor,
//                getElement(), getElement().getContainingFile(),
//                ResolveStates.initial());
//
//        return variants.toArray();
//    }

//    private static boolean isInterfaceOrPointer(PsiElement declaration) {
//        if (declaration instanceof GoTypeNameDeclaration) {
//            GoTypeSpec typeSpec = ((GoTypeNameDeclaration) declaration).getTypeSpec();
//            GoPsiType finalType = resolveToFinalType(typeSpec.getType());
//            if (finalType instanceof GoPsiTypeInterface ||
//                    finalType instanceof GoPsiTypePointer) {
//                return true;
//            }
//        }
//        return false;
//    }
}
