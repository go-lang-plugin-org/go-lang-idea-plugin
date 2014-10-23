package ro.redeul.google.go.lang.psi.resolve.refs;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.resolve.ReferenceWithSolver;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;

public class CompositeLiteralKeyReference
    extends ReferenceWithSolver<GoLiteralIdentifier, CompositeLiteralKeySolver, CompositeLiteralKeyReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER_KEY =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        psiElement(GoElementTypes.LITERAL_COMPOSITE_ELEMENT_KEY)
                            .withParent(
                                psiElement(GoLiteralCompositeElement.class))));

    public static final ElementPattern<GoLiteralIdentifier> MATCHER_ELEMENT =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        psiElement(
                            GoLiteralCompositeElement.class)));


    public CompositeLiteralKeyReference(GoLiteralIdentifier element) {
        super(element);
    }

    @Override
    protected CompositeLiteralKeySolver newSolver() {
        return new CompositeLiteralKeySolver(self());
    }

    @Override
    protected void walkSolver(CompositeLiteralKeySolver solver) {
        GoPsiElement parent = getElementTyped();
        while (parent != null && !(parent instanceof GoLiteralCompositeElement)) {
            parent = (GoPsiElement) parent.getParent();
        }

        if ( parent == null )
            return;

        GoType type = ((GoLiteralCompositeElement)parent).getElementType();

        if (type == null || !(type instanceof GoTypeStruct))
            return;

        GoTypeStruct typeStruct = (GoTypeStruct) type;

        typeStruct.getPsiType().processDeclarations(solver, ResolveState.initial(), null, getElement());
    }

//    private static final ResolveCache.AbstractResolver<CompositeLiteralKeyReference, ResolvingCache.Result> RESOLVER =
//        new ResolveCache.AbstractResolver<CompositeLiteralKeyReference, ResolvingCache.Result>() {
//            @Override
//            public ResolvingCache.Result resolve(@NotNull CompositeLiteralKeyReference psiReference, boolean incompleteCode) {
//                GoTypeStruct typeStruct = psiReference.resolveTypeDefinition();
//                if ( typeStruct == null || typeStruct.getPsiType() == null)
//                    return ResolvingCache.Result.NULL;
//
//                GoLiteralIdentifier element = psiReference.getReferenceElement();
//
//                for (GoTypeStructField field : typeStruct.getPsiType().getFields()) {
//                    for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
//                        if (identifier.getUnqualifiedName().equals(element.getUnqualifiedName()))
//                            return GoResolveResult.fromElement(identifier);
//                    }
//                }
//
//                for (GoTypeStructAnonymousField field : typeStruct.getPsiType().getAnonymousFields()) {
//                    if (field.getFieldName().equals(element.getUnqualifiedName()))
//                        return GoResolveResult.fromElement(field);
//                }
//                return ResolvingCache.Result.NULL;
//            }
//        };

    @Override
    protected CompositeLiteralKeyReference self() { return this; }
}
