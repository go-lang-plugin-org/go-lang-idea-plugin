package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.VarOrConstResolver;
import ro.redeul.google.go.lang.psi.statements.GoLabeledStatement;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

public class VarOrConstReference
    extends GoPsiReference.Single<GoLiteralIdentifier, VarOrConstReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(psiElement(GoLiteralExpression.class));


    private static ResolveCache.AbstractResolver<VarOrConstReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<VarOrConstReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(VarOrConstReference reference, boolean incompleteCode) {
                VarOrConstResolver processor =
                    new VarOrConstResolver(reference);

                PsiScopesUtil.treeWalkUp(
                    processor,
                    reference.getElement().getParent().getParent(),
                    reference.getElement().getContainingFile(),
                    GoResolveStates.initial());

                PsiElement declaration = processor.getChildDeclaration();

                return declaration != null
                    ? new GoResolveResult(declaration)
                    : GoResolveResult.NULL;
            }
        };

    public VarOrConstReference(GoLiteralIdentifier element) {
        super(element, RESOLVER);
    }

    @Override
    protected VarOrConstReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        // Variable shouldn't reference to a label
        if (element != null && element.getParent() instanceof GoLabeledStatement) {
            return false;
        }

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

                if (visiblePackageName != null) {
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
            getElement().getParent().getParent(),
            getElement().getContainingFile(),
            GoResolveStates.initial());

        return variants.toArray();
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
