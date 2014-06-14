package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.VarOrConstResolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

public class VarOrConstReference
        extends GoPsiReference.Single<GoLiteralIdentifier, VarOrConstReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoLiteralExpression.class)
                    );


    private static final ResolveCache.AbstractResolver<VarOrConstReference, GoResolveResult> RESOLVER =
            new ResolveCache.AbstractResolver<VarOrConstReference, GoResolveResult>() {
                @Override
                public GoResolveResult resolve(@NotNull VarOrConstReference reference, boolean incompleteCode) {
                    VarOrConstResolver processor =
                            new VarOrConstResolver(reference);

                    GoPsiScopesUtil.treeWalkUp(
                            processor,
                            reference.getElement().getParent().getParent(),
                            reference.getElement().getContainingFile(),
                            GoResolveStates.initial());

                    return GoResolveResult.fromElement(processor.getChildDeclaration());
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
        return getElement().getManager().areElementsEquivalent(resolve(), element);
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
                    name = "".equals(visiblePackageName) ?
                            name : visiblePackageName + "." + name;
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

        GoPsiScopesUtil.treeWalkUp(
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
