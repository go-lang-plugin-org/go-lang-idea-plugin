package ro.redeul.google.go.lang.psi.impl.expressions.literals.composite;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeArray;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLiteralCompositeValueImpl extends GoPsiElementBase implements GoLiteralCompositeValue
{
    public GoLiteralCompositeValueImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralCompositeElement[] getElements() {
        return findChildrenByClass(GoLiteralCompositeElement.class);
    }

    @NotNull
    @Override
    public GoType getType() {
        PsiElement parent = getParent();

        if ( parent instanceof GoLiteralComposite )
            return GoTypes.fromPsi(((GoLiteralComposite) parent).getLiteralType());

        if (parent instanceof GoLiteralCompositeElement ) {
            GoLiteralCompositeElement element = (GoLiteralCompositeElement) parent;
            final GoLiteralIdentifier elementKey = element.getKey();
            GoType elementType = element.getElementType();


            return elementType.getUnderlyingType().accept(new GoType.Visitor<GoType>(GoType.Unknown) {
                @Override
                public GoType visitArray(GoTypeArray type) {
                    return type.getElementType();
                }

                @Override
                public GoType visitSlice(GoTypeSlice type) {
                    return type.getElementType();
                }

                @Override
                public GoType visitStruct(GoTypeStruct type) {
                    GoPsiTypeStruct structPsi = type.getPsiType();

                    for (GoTypeStructField field : structPsi.getFields()) {
                        for (GoLiteralIdentifier fieldName : field.getIdentifiers()) {
                            if ( fieldName.isEquivalentTo(elementKey) )
                                return GoTypes.fromPsi(field.getType());
                        }
                    }

                    for (GoTypeStructAnonymousField field : structPsi.getAnonymousFields()) {
                        if ( elementKey != null && field.getFieldName().equals(elementKey.getName()))
                            return GoTypes.fromPsi(field.getType());
                    }

                    return GoType.Unknown;
                }
            });
        }

        return GoType.Unknown;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralCompositeVal(this);
    }
}
