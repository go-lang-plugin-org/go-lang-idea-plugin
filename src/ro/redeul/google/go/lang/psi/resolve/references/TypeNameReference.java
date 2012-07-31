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
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.TypeNameResolver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

public class TypeNameReference
    extends GoPsiReference<GoPsiTypeName, TypeNameReference> {
    public static final ElementPattern<GoPsiTypeName> MATCHER =
        psiElement(GoPsiTypeName.class);

    private static ResolveCache.AbstractResolver<TypeNameReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<TypeNameReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(TypeNameReference reference, boolean incompleteCode) {
                TypeNameResolver processor = new TypeNameResolver(reference);

                PsiScopesUtil.treeWalkUp(
                    processor,
                    reference.getElement(),
                    reference.getElement().getContainingFile(),
                    GoResolveStates.initial());

                PsiElement declaration = processor.getDeclaration();

                return
                    declaration != null
                        ? new GoResolveResult(declaration)
                        : GoResolveResult.NULL;
            }
        };

    public TypeNameReference(GoPsiTypeName element) {
        super(element, RESOLVER);
    }

    @Override
    protected TypeNameReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getIdentifier().getCanonicalName();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof GoTypeNameDeclaration) {
            GoTypeNameDeclaration typeNameDecl
                = (GoTypeNameDeclaration) element;

            return matchesVisiblePackageName(typeNameDecl,
                                             getElement().getIdentifier().getName());
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

                    GoPsiElement goDeclaration = (GoPsiElement) declaration;
                    GoPsiElement goChildDeclaration = (GoPsiElement) childDeclaration;

                    variants.add(
                        createLookupElement(
                            goDeclaration,
                            name,
                            goChildDeclaration));
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
