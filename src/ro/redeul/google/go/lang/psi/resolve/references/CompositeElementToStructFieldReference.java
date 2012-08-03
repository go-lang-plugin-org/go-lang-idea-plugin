package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class CompositeElementToStructFieldReference
    extends AbstractStructFieldsReference {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER_KEY =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        psiElement(GoElementTypes.COMPOSITE_LITERAL_ELEMENT_KEY)
                            .withParent(
                                psiElement(
                                    GoLiteralCompositeElement.class))));

    public static final ElementPattern<GoLiteralIdentifier> MATCHER_ELEMENT =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        psiElement(
                            GoLiteralCompositeElement.class)));


    GoLiteralCompositeElement element;

    public CompositeElementToStructFieldReference(GoLiteralCompositeElement element) {
        this(element, element.getKey());
    }

    public CompositeElementToStructFieldReference(GoLiteralCompositeElement element, GoLiteralIdentifier identifier) {
        super(identifier);
        this.element = element;
    }

    @Override
    protected AbstractStructFieldsReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
    }

    @Override
    protected GoTypeStruct resolveTypeDefinition() {
        GoType type = this.element.getElementType();

        if (type == null)
            return null;

        if (type instanceof GoTypeStruct)
            return (GoTypeStruct) type;

        return null;
//        return type.accept(new GoElementVisitorWithData<GoTypeStruct>() {
//            @Override
//            public void visitTypeName(GoPsiTypeName typeName) {
//                GoTypeSpec typeSpec =
//                    GoPsiUtils.resolveSafely(typeName, GoTypeSpec.class);
//
//                if (typeSpec != null) {
//                    if (typeSpec.getType() != null) {
//                        typeSpec.getType().accept(this);
//                    }
//                }
//            }
//
//            public void visitStructType(GoPsiTypeStruct type) {
//                GoType goType = GoTypes.fromPsiType(type);
//                if (goType instanceof GoTypeStruct) {
//                    data = (GoTypeStruct) goType;
//                }
//            }
//        });
    }
}
