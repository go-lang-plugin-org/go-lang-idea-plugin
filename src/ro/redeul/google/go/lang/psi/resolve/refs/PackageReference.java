package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class PackageReference extends ReferenceWithSolver<GoLiteralIdentifier, PackageSolver, PackageReference> {

    public static ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoLiteralExpression.class)
                                    .withParent(
                                            psiElement(GoSelectorExpression.class))
                    );

    public PackageReference(@NotNull GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    protected PackageReference self() {
        return this;
    }

    @Override
    protected PackageSolver newSolver() { return new PackageSolver(self()); }

    @Override
    public void walkSolver(PackageSolver solver) {
        GoPsiScopesUtil.treeWalkUp(
                solver,
                getElement().getParent().getParent(),
                getElement().getContainingFile(),
                ResolveStates.initial());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement resolved = super.resolve();
        return resolved == GoPackages.Invalid ? null : resolved;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return super.isReferenceTo(element);
    }
}
