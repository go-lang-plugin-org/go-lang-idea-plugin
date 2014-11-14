package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.types.interfaces.MethodSetDiscover;
import ro.redeul.google.go.lang.psi.resolve.Reference;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.interfaces.GoTypeInterfaceMethodSet;
import ro.redeul.google.go.lang.psi.typing.GoTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;

import java.util.ArrayList;
import java.util.List;

public class InterfaceMethodReference extends Reference<GoLiteralIdentifier, InterfaceMethodReference> {
    private GoTypeInterface type;

    @Nullable
    @Override
    public PsiElement resolve() {
        return ResolveCache.getInstance(getElement().getProject())
                .resolveWithCaching(this, new ResolveCache.AbstractResolver<InterfaceMethodReference, PsiElement>() {
                    @Override
                    public PsiElement resolve(@NotNull InterfaceMethodReference interfaceMethodReference, boolean incompleteCode) {
                        if ( type == null || type.getPsiType() == null)
                            return null;

                        GoTypeInterfaceMethodSet methodSet = new MethodSetDiscover(type).getMethodSet();

                        for (GoFunctionDeclaration declaration : methodSet.getMethods())
                            if (getElement().getText().equals(declaration.getFunctionName()))
                                return declaration;

                        return null;
                    }
                }, true, false);
    }

    public InterfaceMethodReference(GoLiteralIdentifier element, GoTypeName type) {
        super(element);
        this.type = type.underlyingType(GoTypeInterface.class);
    }

    @Override
    protected InterfaceMethodReference self() {
        return this;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        if ( type == null )
            return EMPTY_ARRAY;

        GoTypeInterfaceMethodSet methodSet = new MethodSetDiscover(type).getMethodSet();

        List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();
        for (GoFunctionDeclaration functionDeclaration : methodSet.getMethods()) {
            variants.add(functionDeclaration.getLookupPresentation());
        }

        return variants.toArray();
    }
}
