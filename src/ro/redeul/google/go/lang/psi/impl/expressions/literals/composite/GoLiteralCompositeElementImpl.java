package ro.redeul.google.go.lang.psi.impl.expressions.literals.composite;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeArray;
import ro.redeul.google.go.lang.psi.typing.GoTypeMap;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.parser.GoElementTypes.LITERAL_COMPOSITE_ELEMENT_KEY;

public class GoLiteralCompositeElementImpl extends GoPsiElementBase implements GoLiteralCompositeElement {

    public GoLiteralCompositeElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralIdentifier getKey() {
        GoExpr keyExpression = getIndex();

        if (keyExpression == null)
            return null;

        if (keyExpression instanceof GoLiteralExpression) {
            GoLiteralExpression expression = (GoLiteralExpression) keyExpression;

            if (expression.getLiteral() instanceof GoLiteralIdentifier) {
                return (GoLiteralIdentifier) expression.getLiteral();
            }
        }

        return null;
    }

    @Override
    public GoExpr getIndex() {
        PsiElement keyNode = findChildByType(LITERAL_COMPOSITE_ELEMENT_KEY);

        return keyNode == null ? null : GoPsiUtils.findChildOfClass(keyNode, GoExpr.class);
    }

    @Override
    public GoExpr getExpressionValue() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public GoLiteralCompositeValue getLiteralValue() {
        return findChildByClass(GoLiteralCompositeValue.class);
    }

    @NotNull
    @Override
    public GoType getType() {
        GoLiteralCompositeValue parentValue = GoPsiUtils.findParentOfType(this, GoLiteralCompositeValue.class);
        if (parentValue == null)
            return GoType.Unknown;

        GoType parentType = parentValue.getType();

        final GoLiteralIdentifier elementKey = getKey();

        return parentType.getUnderlyingType().accept(new GoType.Visitor<GoType>(GoType.Unknown) {
            @Override
            public GoType visitArray(GoTypeArray type) {
                return type.getElementType();
            }

            @Override
            public GoType visitSlice(GoTypeSlice type) {
                return type.getElementType();
            }

            @Override
            public GoType visitMap(GoTypeMap type) {
                return type.getElementType();
            }

            @Override
            public GoType visitStruct(GoTypeStruct type) {
                if (elementKey == null)
                    return GoType.Unknown;

                GoPsiTypeStruct structPsi = type.getPsiType();

                for (GoTypeStructField field : structPsi.getFields()) {
                    for (GoLiteralIdentifier fieldName : field.getIdentifiers()) {
                        if (fieldName.getName().equals(elementKey.getName()))
                            return GoTypes.fromPsi(field.getType());
                    }
                }

                for (GoTypeStructAnonymousField field : structPsi.getAnonymousFields()) {
                    if (field.getFieldName().equals(elementKey.getName()))
                        return GoTypes.fromPsi(field.getType());
                }

                return GoType.Unknown;
            }
        });
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralCompositeElement(this);
    }
}
