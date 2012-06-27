package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.VarOrConstResolver;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementBuilderUtil.createLookupElementBuilder;

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

        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        VarOrConstResolver processor = new VarOrConstResolver(this) {
            @Override
            protected boolean addDeclaration(PsiElement declaration) {
                String name = PsiUtilCore.getName(declaration);

                String visiblePackageName =
                    getState().get(GoResolveStates.VisiblePackageName);

                if ( visiblePackageName != null ) {
                    name = visiblePackageName + "." + name;
                }
                if (name == null) {
                    return true;
                }

                variants.add(createLookupElementBuilder(declaration, name));
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
