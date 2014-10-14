package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.PackageSymbolSolver;
import ro.redeul.google.go.lang.psi.resolve.RefSolver;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

public class PackageSymbolReference extends
        Reference<GoSelectorExpression, GoLiteralIdentifier, PackageSymbolSolver, PackageSymbolReference> {

    private GoPackage myTargetPackage;

    private static final ResolvingCache.Resolver<PackageSymbolReference> RESOLVER =
            ResolvingCache.makeDefault();

    public PackageSymbolReference(@NotNull GoSelectorExpression selectorExpression,
                                  @NotNull GoLiteralIdentifier referenceElement,
                                  @NotNull GoPackage targetPackage) {
        super(selectorExpression, referenceElement, RESOLVER);
        myTargetPackage = targetPackage;
    }

    @Override
    public PackageSymbolSolver newSolver() {
        return new PackageSymbolSolver(this);
    }

    @Override
    public void walkSolver(RefSolver<?, ?> processor) {
        GoPsiScopesUtil.walkPackage(processor, getReferenceElement(), myTargetPackage);
    }

    @Override
    protected PackageSymbolReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() { return "Aaa"; }

    @Override
    public TextRange getRangeInElement() {
        return super.getRangeInElement();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return getElement().getManager().areElementsEquivalent(resolve(), element);
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
