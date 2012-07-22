package ro.redeul.google.go.lang.psi.impl.expressions.literals.composite;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static ro.redeul.google.go.lang.parser.GoElementTypes.COMPOSITE_LITERAL_ELEMENT_KEY;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class GoLiteralCompositeElementImpl extends GoPsiElementBase
    implements GoLiteralCompositeElement {
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
        PsiElement keyNode = findChildByType(COMPOSITE_LITERAL_ELEMENT_KEY);

        if (keyNode == null) {
            return null;
        }

        return GoPsiUtils.findChildOfClass(keyNode, GoExpr.class);
    }

    @Override
    public GoExpr getExpressionValue() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public GoLiteralCompositeValue getLiteralValue() {
        return findChildByClass(GoLiteralCompositeValue.class);
    }

    static
    ElementPattern patternCompositeParent =
        psiElement(GoLiteralCompositeElement.class)
            .withParent(
                psiElement(GoLiteralCompositeValue.class)
                    .withParent(
                        psiElement(GoLiteralComposite.class)));

    static
    ElementPattern patternElementParent =
        psiElement(GoLiteralCompositeElement.class)
            .withParent(
                psiElement(GoLiteralCompositeValue.class)
                    .withParent(
                        psiElement(GoLiteralCompositeElement.class)));

    @Override
    public GoPsiType getElementType() {

        GoPsiType parentType = null;
        if (patternCompositeParent.accepts(this)) {
            GoLiteralComposite literalComposite =
                (GoLiteralComposite) getParent().getParent();

            parentType = literalComposite.getLiteralType();
        }

        if (patternElementParent.accepts(this)) {
            GoLiteralCompositeElement compositeElement =
                (GoLiteralCompositeElement) getParent().getParent();

            if (compositeElement.getKey() != null) {
                GoLiteralIdentifier identifier =
                    resolveSafely(
                        compositeElement.getKey(),
                        psiElement(GoLiteralIdentifier.class)
                            .withParent(psiElement(GoTypeStructField.class)),
                        GoLiteralIdentifier.class);
                if (identifier != null) {
                    parentType = ((GoTypeStructField)identifier.getParent()).getType();
                }
            } else {
                parentType = compositeElement.getElementType();
            }
        }

        if (parentType == null) {
            return parentType;
        }


        while (parentType instanceof GoPsiTypeName) {
            GoTypeSpec typeSpec = resolveSafely(parentType, GoTypeSpec.class);
            parentType = typeSpec.getType();
        }

        if (parentType instanceof GoPsiTypeArray) {
            return ((GoPsiTypeArray) parentType).getElementType();
        }

        if (parentType instanceof GoPsiTypeSlice) {
            return ((GoPsiTypeSlice) parentType).getElementType();
        }

        if (parentType instanceof GoPsiTypeMap) {
            return ((GoPsiTypeMap) parentType).getElementType();
        }

        return parentType;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralCompositeElement(this);
    }


    @Override
    public PsiReference getReference() {
        return null;
    }
}
