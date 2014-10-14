package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.resolve.ResolvingCache;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class CompositeElementOfStructFieldReference
    extends AbstractStructFieldsReference<GoLiteralIdentifier, , CompositeElementOfStructFieldReference> {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER_KEY =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        psiElement(GoElementTypes.COMPOSITE_LITERAL_ELEMENT_KEY)
                            .withParent(
                                psiElement(GoLiteralCompositeElement.class))));

    public static final ElementPattern<GoLiteralIdentifier> MATCHER_ELEMENT =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        psiElement(
                            GoLiteralCompositeElement.class)));


    public CompositeElementOfStructFieldReference(GoLiteralIdentifier element,
                                                  GoLiteralIdentifier identifier) {
        super(element, identifier, RESOLVER);
    }

    private static final ResolveCache.AbstractResolver<CompositeElementOfStructFieldReference, ResolvingCache.Result> RESOLVER =
        new ResolveCache.AbstractResolver<CompositeElementOfStructFieldReference, ResolvingCache.Result>() {
            @Override
            public ResolvingCache.Result resolve(@NotNull CompositeElementOfStructFieldReference psiReference, boolean incompleteCode) {

                GoTypeStruct typeStruct = psiReference.resolveTypeDefinition();

                if ( typeStruct == null || typeStruct.getPsiType() == null)
                    return ResolvingCache.Result.NULL;
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

                return ResolvingCache.Result.NULL;
            }
        };

    @Override
    protected CompositeElementOfStructFieldReference self() {
        return this;
    }

//    @NotNull
//    @Override
//    public String getCanonicalText() {
//        return getReferenceElement().getCanonicalName();
//    }


    @Override
    protected GoTypeStruct resolveTypeDefinition() {
//        GoPsiElement parent = getElement();
//        while (parent != null && !(parent instanceof GoLiteralCompositeElement)) {
//            parent = (GoPsiElement) parent.getParent();
//        }

//        if (parent == null)
//            return null;

//        GoType type = ((GoLiteralCompositeElement)parent).getElementType();

//        if (type == null)
//            return null;
//
//        if (type instanceof GoTypeStruct)
//            return (GoTypeStruct) type;

        return null;
    }
}
