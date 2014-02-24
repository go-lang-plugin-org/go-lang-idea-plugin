package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.resolve.MethodResolver;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.util.LookupElementUtil;

import java.util.*;

public class MethodReference
    extends GoPsiReference.Single<GoSelectorExpression, MethodReference> {

    private Set<GoTypeName> receiverTypes;

    private static final ResolveCache.AbstractResolver<MethodReference, GoResolveResult> RESOLVER =
            new ResolveCache.AbstractResolver<MethodReference, GoResolveResult>() {
                @Override
                public GoResolveResult resolve(@NotNull MethodReference methodReference, boolean incompleteCode) {
                    MethodResolver processor = new MethodResolver(methodReference);

                    GoSelectorExpression element = methodReference.getElement();

                    GoPsiScopesUtil.treeWalkUp(
                            processor,
                            element.getContainingFile().getLastChild(),
                            element.getContainingFile(),
                            GoResolveStates.initial());

                    return GoResolveResult.fromElement(processor.getChildDeclaration());
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
            return TextRange.EMPTY_RANGE;

        return new TextRange(identifier.getStartOffsetInParent(),
                identifier.getStartOffsetInParent() + identifier.getTextLength());
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return ""; // @TODO replace this with something more meaningful
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return getElement().getManager().areElementsEquivalent(resolve(), element);
    }


    @NotNull
    @Override
    public Object[] getVariants() {
        Set<GoTypeName> resolverTypeNames = resolveBaseReceiverTypes();
        if (resolverTypeNames.size() == 0)
            return LookupElementBuilder.EMPTY_ARRAY;

        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        MethodResolver processor = new MethodResolver(this) {
            @Override
            protected boolean addDeclaration(PsiElement declaration, PsiElement child) {
                String name = PsiUtilCore.getName(declaration);

                if (child == null) {
                    return true;
                }

                variants.add(LookupElementUtil.createLookupElement(
                    (GoPsiElement) declaration, name,
                    (GoPsiElement) child));
                return true;
            }
        };

        GoPsiScopesUtil.treeWalkUp(
            processor,
            getElement().getContainingFile().getLastChild(),
            getElement().getContainingFile(),
            GoResolveStates.initial());

        return variants.toArray(new LookupElementBuilder[variants.size()]);
    }

    @NotNull
    public Set<GoTypeName> resolveBaseReceiverTypes() {
        if ( receiverTypes != null )
            return receiverTypes;

        receiverTypes = new HashSet<GoTypeName>();

        GoType[] types = getElement().getBaseExpression().getType();

        if (types.length < 1)
            return receiverTypes;

        GoType type = types[0];
        if (type instanceof GoTypePointer)
            type = ((GoTypePointer) type).getTargetType();

        if (!(type instanceof GoTypeName))
            return receiverTypes;

        GoTypeName typeName = (GoTypeName) type;

        Queue<GoTypeName> typeNamesToExplore = new LinkedList<GoTypeName>();
        typeNamesToExplore.offer(typeName);

        while ( ! typeNamesToExplore.isEmpty() ) {
            GoTypeName currentTypeName = typeNamesToExplore.poll();

            receiverTypes.add(currentTypeName);

            if ( !(currentTypeName.getDefinition() instanceof GoTypeStruct) )
                continue;

            GoTypeStruct typeStruct = (GoTypeStruct) currentTypeName.getDefinition();
            for (GoTypeStructAnonymousField field : typeStruct.getPsiType().getAnonymousFields()) {
                GoPsiType psiType = field.getType();
                if ( psiType == null)
                    continue;
                if ( psiType instanceof GoPsiTypePointer) {
                    psiType = ((GoPsiTypePointer) psiType).getTargetType();
                }

                GoType embeddedType = GoTypes.fromPsiType(psiType);
                if (embeddedType == null || !(embeddedType instanceof GoTypeName))
                    continue;

                GoTypeName embeddedTypeName = (GoTypeName) embeddedType;
                if (! receiverTypes.contains(embeddedTypeName) )
                    typeNamesToExplore.offer(embeddedTypeName);

                receiverTypes.add(embeddedTypeName);
            }
        }

        return receiverTypes;
    }

    public boolean isSoft() {
        return false;
    }
}
