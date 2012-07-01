package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeInterface;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

public class InterfaceMethodReference extends GoPsiReference<GoLiteralIdentifier> {

    GoTypeInterface type;
    GoSelectorExpression selector;

    public InterfaceMethodReference(GoSelectorExpression element) {
        super(element.getIdentifier());

        selector = element;
        type = findTypeInterfaceDeclaration();
    }


    private GoTypeInterface findTypeInterfaceDeclaration() {
        GoType type = selector.getBaseExpression().getType()[0];
        while ( type != null && ! (type instanceof GoTypeInterface) ) {
            GoTypeSpec typeSpec = GoPsiUtils.resolveSafely(type, GoTypeSpec.class);
            if ( typeSpec != null ) {
                type = typeSpec.getType();
            }
        }

        if ( type == null )
            return null;

        return (GoTypeInterface)type;
    }

    @Override
    public PsiElement resolve() {
        GoLiteralIdentifier identifier = selector.getIdentifier();

        if ( identifier == null ) {
            return null;
        }

        String name = identifier.getName();

        if (name == null) {
            return null;
        }

        for (GoMethodDeclaration declaration : type.getMethodDeclarations()) {
            if (name.equals(declaration.getFunctionName())) {
                return declaration;
            }
        }

        return null;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
