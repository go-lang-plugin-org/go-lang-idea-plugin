package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.ResolveState;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public class TypeNameReference extends ReferenceWithSolver<GoLiteralIdentifier, TypeNameSolver, TypeNameReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class).withParent(psiElement(GoPsiTypeName.class));


    public static final PsiElementPattern.Capture<GoLiteralIdentifier> TYPE_NAME_MATCHER =
            psiElement(GoLiteralIdentifier.class).insideStarting(psiElement(GoPsiTypeName.class));

    public static final ElementPattern<GoLiteralIdentifier> PACKAGE_QUALIFIER =
            psiElement(GoLiteralIdentifier.class)
                    .insideStarting(psiElement(GoPsiTypeName.class))
                    .beforeLeaf(psiElement(GoTokenTypes.oDOT));

    public static final ElementPattern<GoLiteralIdentifier> QUALIFIED_NAME =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(psiElement(GoPsiTypeName.class))
                    .afterSibling(
                            psiElement(GoTokenTypes.oDOT)
                                    .afterSibling(
                                            psiElement(GoLiteralIdentifier.class)
                                                    .afterSibling(
                                                            psiElement().isNull())));

    @SuppressWarnings("unchecked")
    private static final ElementPattern<GoLiteralIdentifier> TYPE_IN_METHOD_RECEIVER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoPsiTypeName.class).withParent(
                                    or(
                                            psiElement(GoMethodReceiver.class),
                                            psiElement(GoPsiTypePointer.class).withParent(psiElement(GoMethodReceiver.class))
                                    )
                            ));

    private final GoPackage goPackage;
    private final GoPackage srcPackage;

    public TypeNameReference(GoLiteralIdentifier element) {
        this(element, GoPackages.getPackageFor(element));
    }

    public TypeNameReference(GoLiteralIdentifier element, GoPackage goPackage) {
        super(element);
        this.goPackage = goPackage;
        this.srcPackage = GoPackages.getPackageFor(element);
    }

    @Override
    protected TypeNameReference self() {
        return this;
    }

    @Override
    public TypeNameSolver newSolver() {
        return new TypeNameSolver(self(), TYPE_IN_METHOD_RECEIVER.accepts(getElement()));
    }

    @Override
    public void walkSolver(TypeNameSolver solver) {
        if (srcPackage == goPackage)
            GoPsiScopesUtil.treeWalkUp(solver, getElement(), null, ResolveStates.initial());
        else
            GoPsiScopesUtil.walkPackageExports(solver, getElement(), goPackage);
    }
}
