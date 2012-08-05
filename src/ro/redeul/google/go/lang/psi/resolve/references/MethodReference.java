package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.MethodResolver;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.util.LookupElementUtil;
import static ro.redeul.google.go.lang.completion.GoCompletionContributor.DUMMY_IDENTIFIER;

public class MethodReference
    extends GoPsiReference<GoSelectorExpression, MethodReference> {

    GoTypeName baseTypeName;

    private static ResolveCache.AbstractResolver<MethodReference, GoResolveResult> RESOLVER =
        new ResolveCache.AbstractResolver<MethodReference, GoResolveResult>() {
            @Override
            public GoResolveResult resolve(MethodReference methodReference, boolean incompleteCode) {
                GoTypeName baseTypeName = methodReference.resolveBaseExpressionType();

                if (baseTypeName == null)
                    return null;

                MethodResolver processor = new MethodResolver(methodReference);

                GoSelectorExpression element = methodReference.getElement();

                PsiScopesUtil.treeWalkUp(
                    processor,
                    element.getContainingFile().getLastChild(),
                    element.getContainingFile(),
                    GoResolveStates.initial());

                PsiElement declaration = processor.getChildDeclaration();
                return declaration != null
                    ? new GoResolveResult(declaration)
                    : GoResolveResult.NULL;
            }
        };

    public MethodReference(@NotNull GoSelectorExpression element) {
        super(element, RESOLVER);
    }

    @Override
    protected MethodReference self() {
        return this;
    }

    @Override
    public TextRange getRangeInElement() {
        GoLiteralIdentifier identifier = getElement().getIdentifier();
        if (identifier == null)
            return null;

        return new TextRange(identifier.getStartOffsetInParent(),
                             identifier.getStartOffsetInParent() + identifier.getTextLength());
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {

        if (!(element instanceof GoMethodDeclaration))
            return false;

        GoMethodDeclaration declaration = (GoMethodDeclaration) element;

        GoPsiType receiverType = declaration.getMethodReceiver().getType();

        if (receiverType == null)
            return false;

        if (receiverType instanceof GoPsiTypePointer) {
            receiverType = ((GoPsiTypePointer) receiverType).getTargetType();
        }

        if (!(receiverType instanceof GoPsiTypeName))
            return false;

        GoPsiTypeName methodTypeName = (GoPsiTypeName) receiverType;

        if (baseTypeName != null && baseTypeName.getName() != null &&
            baseTypeName.getName().equals(methodTypeName.getName())) {

            String methodName = declaration.getFunctionName();
            GoLiteralIdentifier identifier = getElement().getIdentifier();
            if (identifier != null ) {
                String referenceName = identifier.getUnqualifiedName();

                return referenceName.contains(DUMMY_IDENTIFIER) ||
                    referenceName.equals(methodName);
            }
        }

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @NotNull
    @Override
    public Object[] getVariants() {
        GoTypeName baseTypName = resolveBaseExpressionType();
        if (baseTypeName == null)
            return LookupElementBuilder.EMPTY_ARRAY;

        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        MethodResolver processor = new MethodResolver(this) {
            @Override
            protected boolean addDeclaration(PsiElement declaration, PsiElement child) {
                String name = PsiUtilCore.getName(declaration);

                variants.add(LookupElementUtil.createLookupElement(
                    (GoPsiElement) declaration, name,
                    (GoPsiElement) child));
                return true;
            }
        };

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement().getContainingFile().getLastChild(),
            getElement().getContainingFile(),
            GoResolveStates.initial());

        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }

    private GoTypeName resolveBaseExpressionType() {
        GoType[] types = getElement().getBaseExpression().getType();

        if (types.length < 1)
            return null;

        GoType type = types[0];
        if ( type instanceof GoTypePointer)
            type = ((GoTypePointer) type).getTargetType();

        if (!(type instanceof GoTypeName))
            return null;

        baseTypeName = (GoTypeName) type;
        return baseTypeName;
    }

    public boolean isSoft() {
        return false;
    }
}
