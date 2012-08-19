package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;

public class InterfaceMethodReference extends
                                      GoPsiReference.Single<GoSelectorExpression, InterfaceMethodReference> {

    GoTypeInterface type;
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

                GoTypeInterface type = interfaceMethodReference.type;
                GoFunctionDeclaration[] functionDeclarations =
                    type.getPsiType().getFunctionDeclarations();

                for (GoFunctionDeclaration declaration : functionDeclarations) {
                    if (name.equals(declaration.getFunctionName())) {
                        return new GoResolveResult(declaration.getNameIdentifier());
                    }
                }

                return GoResolveResult.NULL;
            }
        };


    public InterfaceMethodReference(GoSelectorExpression element) {
        super(element, RESOLVER);

        selector = element;
        type = findTypeInterfaceDeclaration();
    }

    @Override
    public TextRange getRangeInElement() {
        GoLiteralIdentifier identifier = getElement().getIdentifier();
        if (identifier == null)
            return null;

        return new TextRange(identifier.getStartOffsetInParent(),
                             identifier.getStartOffsetInParent() + identifier.getTextLength());
    }

    @Override
    protected InterfaceMethodReference self() {
        return this;
    }

    private GoTypeInterface findTypeInterfaceDeclaration() {
        GoType type = selector.getBaseExpression().getType()[0];
        while ( type != null && ! (type instanceof GoTypeInterface) ) {
            if ( type instanceof GoTypeName) {
                type = ((GoTypeName) type).getDefinition();
            } else if ( type instanceof GoTypePointer ) {
                type = ((GoTypePointer) type).getTargetType();
            } else {
                type = null;
            }
        }

        if ( type == null )
            return null;

        return (GoTypeInterface)type;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        GoLiteralIdentifier identifier = getElement().getIdentifier();
        return identifier != null ? identifier.getCanonicalName() : "";
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        GoFunctionDeclaration[] methods = type.getPsiType().getFunctionDeclarations();

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
