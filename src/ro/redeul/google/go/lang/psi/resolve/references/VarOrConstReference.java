package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.VarOrConstResolver;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class VarOrConstReference extends GoPsiReference<GoLiteralIdentifier> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(psiElement(GoLiteralExpression.class));

    public VarOrConstReference(GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    public PsiElement resolve() {
        VarOrConstResolver processor = new VarOrConstResolver(this);

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement().getParent().getParent(), getElement().getContainingFile(),
            GoResolveStates.initial());

        return processor.getDeclaration();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return matchesVisiblePackageName(element, getElement().getName());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
