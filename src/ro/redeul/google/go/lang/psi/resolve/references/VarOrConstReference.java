package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.ResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.VarOrConstResolver;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

public class VarOrConstReference
        extends GoPsiReference.Single<GoLiteralIdentifier, VarOrConstReference> {

    GoPackage targetPackage;

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoLiteralExpression.class)
                    );

    private static final ResolveCache.AbstractResolver<VarOrConstReference, GoResolveResult> RESOLVER =
            new ResolveCache.AbstractResolver<VarOrConstReference, GoResolveResult>() {
                @Override
                public GoResolveResult resolve(@NotNull VarOrConstReference ref, boolean incompleteCode) {
                    VarOrConstResolver processor = new VarOrConstResolver(ref);

                    if (ref.targetPackage == null)
                        GoPsiScopesUtil.treeWalkUp(
                                processor,
                                ref.getElement().getParent().getParent(),
                                ref.getElement().getContainingFile(),
                                ResolveStates.initial());
                    else
                        GoPsiScopesUtil.packageWalk(
                                ref.targetPackage, processor, ref.getElement(),
                                ResolveStates.packageExports()
                        );

                    return GoResolveResult.fromElement(processor.getChildDeclaration());
                }
            };

    public VarOrConstReference(GoLiteralIdentifier element) {
        this(element, null);
    }

    public VarOrConstReference(GoLiteralIdentifier element, GoPackage targetPackage) {
        super(element, RESOLVER);
        this.targetPackage = targetPackage;
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

                String visiblePackageName = "";
//                        getState().get(ResolveStates.VisiblePackageName);

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
                ResolveStates.initial());

        return variants.toArray();
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
