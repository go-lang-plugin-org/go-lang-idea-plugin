package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSliceExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.hasPrevSiblingOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceOrComment;

public class GoSliceExpressionImpl extends GoExpressionBase
    implements GoSliceExpression {

    public GoSliceExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSliceExpression(this);
    }

    @Override
    protected GoType[] resolveTypes() {
        return getBaseExpression().getType();
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class, 0);
    }

    @Override
    public GoExpr getFirstIndex() {
        GoExpr expr = findChildByClass(GoExpr.class, 1);
        if ( expr != null && hasPrevSiblingOfType(expr, GoTokenTypes.pLBRACK)) {
            return expr;
        }

        return null;
    }

    @Override
    public GoExpr getSecondIndex() {
        GoExpr expressions[] = findChildrenByClass(GoExpr.class);

        PsiElement firstColon = expressions[0].getNextSibling();

        while (firstColon != null &&
                firstColon.getNode().getElementType() != GoTokenTypes.oCOLON
            ) {
            firstColon = firstColon.getNextSibling();
        }

        if (firstColon == null) {
            return null;
        }

        PsiElement secondStop = firstColon.getNextSibling();

        while (secondStop != null &&
                isWhiteSpaceOrComment(secondStop) &&
                secondStop.getNode().getElementType() != GoTokenTypes.oCOLON &&
                secondStop.getNode().getElementType() != GoTokenTypes.pRBRACK) {
            secondStop = secondStop.getNextSibling();
        }

        if (secondStop == null) {
            return null;
        }

        if (secondStop.getNode().getElementType() == GoTokenTypes.oCOLON ||
                secondStop.getNode().getElementType() == GoTokenTypes.pRBRACK) {
            return null;
        }

        return (GoExpr) secondStop;
    }

    @Override
    public GoExpr getCapacity() {
        GoExpr expressions[] = findChildrenByClass(GoExpr.class);

        PsiElement firstColon = expressions[0].getNextSibling();

        while (firstColon != null && firstColon.getNode().getElementType() != GoTokenTypes.oCOLON) {
            firstColon = firstColon.getNextSibling();
        }

        if (firstColon == null) {
            return null;
        }

        PsiElement secondStop = firstColon.getNextSibling();

        while (secondStop != null &&
                secondStop.getNode().getElementType() != GoTokenTypes.oCOLON &&
                secondStop.getNode().getElementType() != GoTokenTypes.pRBRACK) {
            secondStop = secondStop.getNextSibling();
        }

        if (secondStop == null) {
            return null;
        }

        PsiElement elem = secondStop.getNextSibling();

        while (elem != null &&
                isWhiteSpaceOrComment(elem) &&
                elem.getNode().getElementType() != GoTokenTypes.pRBRACK) {
            elem = elem.getNextSibling();
        }

        if (elem == null || elem.getNode().getElementType() == GoTokenTypes.pRBRACK) {
            return null;
        }

        return (GoExpr) elem;
    }

    @Override
    public boolean isConstantExpression() {
        return false;
    }
}
