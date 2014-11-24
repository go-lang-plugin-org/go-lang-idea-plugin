package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.ResolveState;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public class TypeNameReference extends ReferenceWithSolver<GoPsiTypeName, TypeNameSolver, TypeNameReference> {

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
    private final GoPackage srcPackage;

    public TypeNameReference(GoPsiTypeName element) {
        this(element, null);
    }

    public TypeNameReference(GoPsiTypeName element, GoPackage goPackage) {
        super(element);
        this.goPackage = goPackage;
        this.srcPackage = GoPackages.getPackageFor(element);
    }

    @Override
    protected TypeNameReference self() { return this; }

    @Override
    public TypeNameSolver newSolver() {
        return new TypeNameSolver(self(), TYPE_IN_METHOD_RECEIVER.accepts(getElement()));
    }

    @Override
    public void walkSolver(TypeNameSolver solver) {
        if ( srcPackage == goPackage)
            GoPsiScopesUtil.walkPackage(solver, ResolveState.initial(), null, srcPackage);
        else
            GoPsiScopesUtil.walkPackageExports(solver, getElement(), goPackage);
    }
}
