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
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.parser.GoElementTypes.LITERAL_COMPOSITE_ELEMENT_KEY;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveTypeSpec;

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
        PsiElement keyNode = findChildByType(LITERAL_COMPOSITE_ELEMENT_KEY);

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

    private static final
    ElementPattern patternCompositeParent =
        psiElement(GoLiteralCompositeElement.class)
            .withParent(
                psiElement(GoLiteralCompositeValue.class)
                    .withParent(
                        psiElement(GoLiteralComposite.class)));

    private static final
    ElementPattern patternElementParent =
        psiElement(GoLiteralCompositeElement.class)
            .withParent(
                psiElement(GoLiteralCompositeValue.class)
                    .withParent(
                        psiElement(GoLiteralCompositeElement.class)));

    @Override
    public GoType getElementType() {

        GoType parentType = null;
        if (patternCompositeParent.accepts(this)) {
            GoLiteralComposite literalComposite =
                (GoLiteralComposite) getParent().getParent();

            parentType = GoTypes.fromPsiType(literalComposite.getLiteralType());
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
                    parentType = GoTypes.fromPsiType(
                        ((GoTypeStructField) identifier.getParent()).getType());
                }
            } else {
                parentType = compositeElement.getElementType();
            }
        }

        if (parentType == null) {
            return null;
        }

        while (parentType != null && parentType instanceof GoTypeName) {
            GoTypeName goTypeName = (GoTypeName) parentType;
            GoTypeSpec typeSpec = resolveTypeSpec(goTypeName.getPsiType());

            if (typeSpec != null)
                parentType = GoTypes.fromPsiType(typeSpec.getType());

            if (typeSpec == null)
                parentType = null;

        }

        if (parentType instanceof GoTypeArray) {
            return ((GoTypeArray) parentType).getElementType();
        }

        if (parentType instanceof GoTypeSlice) {
            return ((GoTypeSlice) parentType).getElementType();
        }

        if (parentType instanceof GoTypeMap) {
            return ((GoTypeMap) parentType).getElementType();
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
