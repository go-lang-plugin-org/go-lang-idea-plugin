package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.TypeNameResolver;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static ro.redeul.google.go.lang.completion.GoCompletionUtil.getImportedPackagesNames;
import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

public class TypeNameReference
        extends GoPsiReference.Single<GoPsiTypeName, TypeNameReference> {
    public static final ElementPattern<GoPsiTypeName> MATCHER =
            psiElement(GoPsiTypeName.class);

    @SuppressWarnings("unchecked")
    private static final ElementPattern<GoPsiTypeName> TYPE_IN_METHOD_RECEIVER =
            psiElement(GoPsiTypeName.class).withParent(
                    or(
                            psiElement(GoMethodReceiver.class),
                            psiElement(GoPsiTypePointer.class).withParent(psiElement(GoMethodReceiver.class))
                    )
            );

    private static final ResolveCache.AbstractResolver<TypeNameReference, GoResolveResult> RESOLVER =
            new ResolveCache.AbstractResolver<TypeNameReference, GoResolveResult>() {
                @Override
                public GoResolveResult resolve(@NotNull TypeNameReference reference, boolean incompleteCode) {
                    TypeNameResolver processor = new TypeNameResolver(reference);

                    GoPsiScopesUtil.treeWalkUp(
                            processor,
                            reference.getElement(),
                            reference.getElement().getContainingFile(),
                            GoResolveStates.initial());

                    return GoResolveResult.fromElement(processor.getDeclaration());
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
        return getElement().getManager().areElementsEquivalent(resolve(), element);
    }

    @NotNull
    @Override
    public Object[] getVariants() {

        final List<LookupElement> variants = new ArrayList<LookupElement>();

        // According to the spec, method receiver type "T" could not be an interface or a pointer.
        final boolean rejectInterfaceAndPointer = TYPE_IN_METHOD_RECEIVER.accepts(getElement());
        Collections.addAll(variants, getImportedPackagesNames(getElement().getContainingFile()));

        TypeNameResolver processor =
                new TypeNameResolver(this) {
                    @Override
                    protected boolean addDeclaration(PsiElement declaration, PsiElement childDeclaration) {
                        if (rejectInterfaceAndPointer && isInterfaceOrPointer(declaration)) {
                            return true;
                        }

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

        GoPsiScopesUtil.treeWalkUp(
                processor,
                getElement(), getElement().getContainingFile(),
                GoResolveStates.initial());

        return variants.toArray();
    }

    private static boolean isInterfaceOrPointer(PsiElement declaration) {
        if (declaration instanceof GoTypeNameDeclaration) {
            GoTypeSpec typeSpec = ((GoTypeNameDeclaration) declaration).getTypeSpec();
            GoPsiType finalType = resolveToFinalType(typeSpec.getType());
            if (finalType instanceof GoPsiTypeInterface ||
                    finalType instanceof GoPsiTypePointer) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
