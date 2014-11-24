package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public class FunctionOrTypeNameReference extends ReferenceWithSolver<GoLiteralIdentifier, FunctionOrTypeNameSolver, FunctionOrTypeNameReference> {

    public static final PsiElementPattern.Capture<PsiElement> MATCHER = psiElement()
            .withParent(
                    psiElement(GoLiteralExpression.class)
                            .atStartOf(
                                    or(
                                            psiElement(GoBuiltinCallOrConversionExpression.class),
                                            psiElement(GoCallOrConvExpression.class))))
            .withSuperParent(2,
                    or(
                            psiElement(GoBuiltinCallOrConversionExpression.class),
                            psiElement(GoCallOrConvExpression.class))
            );

    private final GoPackage srcPackage;

    public FunctionOrTypeNameReference(GoLiteralIdentifier element) {
        super(element);
        this.srcPackage = GoPackages.getPackageFor(element);
    }

    @Override
    protected FunctionOrTypeNameReference self() { return this; }

    @Override
    public FunctionOrTypeNameSolver newSolver() { return new FunctionOrTypeNameSolver(this); }

    @Override
    public void walkSolver(FunctionOrTypeNameSolver solver) {
        GoPsiScopesUtil.walkPackage(solver, ResolveState.initial(), getElement(), srcPackage);
    }
}
