package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.GoTypeStruct;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;

public class CompositeElementToStructFieldReference extends AbstractStructFieldsReference {

    public static final ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(
                        psiElement(GoElementTypes.COMPOSITE_LITERAL_ELEMENT_KEY)
                            .withParent(
                                psiElement(GoLiteralCompositeElement.class))));

    GoLiteralCompositeElement element;

    public CompositeElementToStructFieldReference(GoLiteralCompositeElement element) {
        super(element.getKey());

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

        final GoTypeStruct[] struct = new GoTypeStruct[1];
        type.accept(new GoElementVisitor() {
            @Override
            public void visitTypeName(GoTypeName typeName) {
                GoTypeSpec typeSpec =
                    GoPsiUtils.resolveSafely(typeName, GoTypeSpec.class);

                if (typeSpec != null) {
                    if (typeSpec.getType() != null) {
                        typeSpec.getType().accept(this);
                    }
                }
            }

            @Override
            public void visitStructType(GoTypeStruct type) {
                struct[0] = type;
            }
        });

        return struct[0];
    }
}
