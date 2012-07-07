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
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static ro.redeul.google.go.lang.parser.GoElementTypes.COMPOSITE_LITERAL_ELEMENT_KEY;

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

        if ( keyExpression instanceof GoLiteralExpression ) {
            GoLiteralExpression expression = (GoLiteralExpression) keyExpression;

            if ( expression.getLiteral() instanceof GoLiteralIdentifier ) {
                return (GoLiteralIdentifier) expression.getLiteral();
            }
        }

        return null;
    }

    @Override
    public GoExpr getIndex() {
        PsiElement keyNode = findChildByType(COMPOSITE_LITERAL_ELEMENT_KEY);

        if ( keyNode == null ) {
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

    ElementPattern pattern =
        psiElement(GoLiteralCompositeElement.class)
            .withParent(
                psiElement(GoLiteralCompositeValue.class)
                    .withParent(psiElement(GoLiteralComposite.class))
            );

    @Override
    public GoType getElementType() {
        if (pattern.accepts(this)) {
            return ((GoLiteralComposite)getParent().getParent()).getLiteralType();
        }

        return null;
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
