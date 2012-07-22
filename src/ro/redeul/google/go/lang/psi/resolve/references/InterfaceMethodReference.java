package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoType;

public class InterfaceMethodReference extends GoPsiReference<GoLiteralIdentifier, InterfaceMethodReference> {

    GoPsiTypeInterface type;
    GoSelectorExpression selector;

    private static ResolveCache.AbstractResolver<InterfaceMethodReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<InterfaceMethodReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(InterfaceMethodReference interfaceMethodReference, boolean incompleteCode) {
                GoSelectorExpression selector = interfaceMethodReference.selector;
                GoLiteralIdentifier identifier = selector.getIdentifier();

                if (identifier == null) {
                    return GoResolveResult.NULL;
                }

                String name = identifier.getName();

                if (name == null) {
                    return GoResolveResult.NULL;
                }

                GoPsiTypeInterface type = interfaceMethodReference.type;
                for (GoMethodDeclaration declaration : type.getMethodDeclarations()) {
                    if (name.equals(declaration.getFunctionName())) {
                        return new GoResolveResult(declaration);
                    }
                }

                return GoResolveResult.NULL;
            }
        };


    public InterfaceMethodReference(GoSelectorExpression element) {
        super(element.getIdentifier(), RESOLVER);

        selector = element;
        type = findTypeInterfaceDeclaration();
    }


    @Override
    protected InterfaceMethodReference self() {
        return this;
    }

    private GoPsiTypeInterface findTypeInterfaceDeclaration() {
        GoType type = selector.getBaseExpression().getType()[0];
        while ( type != null && ! (type instanceof GoPsiTypeInterface) ) {
            // TODO: fixed compilation here
//            GoTypeSpec typeSpec = GoPsiUtils.resolveSafely(type, GoTypeSpec.class);
//            if ( typeSpec != null ) {
//                type = typeSpec.getType();
//            }
        }

        if ( type == null )
            return null;

        return (GoPsiTypeInterface)type;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        GoMethodDeclaration[] methods = type.getMethodDeclarations();

        LookupElementBuilder variants[] = new LookupElementBuilder[methods.length];
        for (int i = 0; i < methods.length; i++) {
            variants[i] = methods[i].getCompletionPresentation();
        }

        return variants;
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
