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
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.TypeNameResolver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class TypeNameReference extends GoPsiReference<GoTypeName> {
    public static final ElementPattern<GoTypeName> MATCHER =
        psiElement(GoTypeName.class);

    public TypeNameReference(GoTypeName element) {
        super(element);
    }

    @Override
    public PsiElement resolve() {
        TypeNameResolver processor =
            new TypeNameResolver(this);

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement(), getElement().getContainingFile(),
            GoResolveStates.initial());

        return processor.getDeclaration();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof GoTypeNameDeclaration) {
            GoTypeNameDeclaration typeNameDecl
                = (GoTypeNameDeclaration)element;

            return matchesVisiblePackageName(typeNameDecl, getElement().getName());
        }

        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {

        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        TypeNameResolver processor =
            new TypeNameResolver(this) {
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

                    GoPsiElement goDeclaration = (GoPsiElement) declaration;
                    variants.add(goDeclaration.getCompletionPresentation());
                    return true;

                }
            };

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement(), getElement().getContainingFile(),
            GoResolveStates.initial());

        return variants.toArray();
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
