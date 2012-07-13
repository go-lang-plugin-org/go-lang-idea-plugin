package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.VarOrConstResolver;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

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

        return processor.getChildDeclaration();
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return matchesVisiblePackageName(element, getElement().getName());
    }

    @NotNull
    @Override
    public Object[] getVariants() {

        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        VarOrConstResolver processor = new VarOrConstResolver(this) {
            @Override
            protected boolean addDeclaration(PsiElement declaration, PsiElement childDeclaration) {
                String name = PsiUtilCore.getName(declaration);

                String visiblePackageName =
                    getState().get(GoResolveStates.VisiblePackageName);

                if ( visiblePackageName != null ) {
                    name = visiblePackageName + "." + name;
                }
                if (name == null) {
                    return true;
                }

                GoPsiElement goPsi = (GoPsiElement) declaration;
                GoPsiElement goChildPsi = (GoPsiElement) childDeclaration;
                variants.add(createLookupElement(goPsi, name, goChildPsi));
                return true;
            }
        };

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement().getParent().getParent(), getElement().getContainingFile(),
            GoResolveStates.initial());

        return variants.toArray();
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
