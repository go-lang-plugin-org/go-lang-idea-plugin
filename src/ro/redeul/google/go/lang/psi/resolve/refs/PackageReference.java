package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class PackageReference extends ReferenceWithSolver<GoLiteralIdentifier, PackageSolver, PackageReference> {

    public static ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoLiteralExpression.class)
                    );

    private static final ResolvingCache.Resolver<PackageReference> RESOLVER = ResolvingCache.makeDefault();

    public PackageReference(@NotNull GoLiteralIdentifier element) {
//        super(element, element, RESOLVER);
    }

    @Override
    protected PackageReference self() {
        return this;
    }

    @Override
    protected PackageSolver newSolver() { return new PackageSolver(); }

    @Override
    public TextRange getRangeInElement() {
        return null;
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void walkSolver(PackageSolver solver) {
        GoPsiScopesUtil.treeWalkUp(
                solver,
                getElement().getParent().getParent(),
                getElement().getContainingFile(),
                ResolveStates.initial());
    }


    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;
    }

}
