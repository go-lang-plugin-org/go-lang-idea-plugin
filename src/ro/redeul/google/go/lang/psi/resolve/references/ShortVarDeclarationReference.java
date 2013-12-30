package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.ShortVarDeclarationResolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * ShortVarDeclarationReference is a reference of identifier defined in short variable declaration.
 *
 * According to spec, short variable declaration may redeclare variables.
 * If the variable is redeclared in current short variable declaration, method {@link #resolve}
 * returns the identifier where it's declared. Otherwise, null is returned.
 */
public class ShortVarDeclarationReference
        extends GoPsiReference.Single<GoLiteralIdentifier, ShortVarDeclarationReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
            psiElement(GoLiteralIdentifier.class)
                    .withParent(
                            psiElement(GoShortVarDeclaration.class)
                    );

    private static final ResolveCache.AbstractResolver<ShortVarDeclarationReference, GoResolveResult> RESOLVER =
            new ResolveCache.AbstractResolver<ShortVarDeclarationReference, GoResolveResult>() {
                @Override
                public GoResolveResult resolve(@NotNull ShortVarDeclarationReference reference, boolean incompleteCode) {
                    GoLiteralIdentifier element = reference.getElement();
                    PsiElement parent = element.getParent();
                    if (!(parent instanceof GoShortVarDeclaration)) {
                        return GoResolveResult.NULL;
                    }

                    GoLiteralIdentifier identifier = reference.getElement();
                    PsiElement resolve = ShortVarDeclarationResolver.resolve(identifier);
                    if (resolve == null) {
                        return GoResolveResult.NULL;
                    }
                    return GoResolveResult.fromElement(resolve);
                }
            };


    public ShortVarDeclarationReference(@NotNull GoLiteralIdentifier element) {
        super(element, RESOLVER);
    }

    @Override
    protected ShortVarDeclarationReference self() {
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
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
