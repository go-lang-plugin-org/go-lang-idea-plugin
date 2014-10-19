package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class PackageSymbolReference
        extends ReferenceWithSolver<GoLiteralIdentifier, PackageSymbolSolver, PackageSymbolReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoSelectorExpression.class)
                                    .withFirstNonWhitespaceChild(psiElement(GoLiteralExpression.class))
                                    .withLastChild(psiElement(GoLiteralIdentifier.class))
                    ).afterSibling(psiElement(GoLiteralExpression.class));

    private GoPackage myTargetPackage;

    public PackageSymbolReference(@NotNull GoLiteralIdentifier element,
                                  @NotNull GoPackage targetPackage) {
        super(element);
        myTargetPackage = targetPackage;
    }

    @Override
    protected PackageSymbolReference self() {
        return this;
    }

    @Override
    public PackageSymbolSolver newSolver() {
        return new PackageSymbolSolver(this);
    }

    @Override
    protected void walkSolver(PackageSymbolSolver solver) {
        GoPsiScopesUtil.walkPackage(solver, getElement(), myTargetPackage);
    }
}
