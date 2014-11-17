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
import ro.redeul.google.go.lang.psi.typing.GoTypeArray;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypePrimitive;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.typing.TypeVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.hasPrevSiblingOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceOrComment;

public class GoSliceExpressionImpl extends GoExpressionBase implements GoSliceExpression {

    public GoSliceExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSliceExpression(this);
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        GoType baseType = GoTypes.get(getBaseExpression().getType());

        GoType elementType = baseType.underlyingType().accept(new TypeVisitor<GoType>(GoType.Unknown) {
            @Override
            public GoType visitSlice(GoTypeSlice type) {
                return type.getElementType();
            }

            @Override
            public GoType visitArray(GoTypeArray type) {
                return type.getElementType();
            }

            @Override
            public GoType visitPrimitive(GoTypePrimitive type) {
                switch (type.getType()) {
                    case String:
                        return types().getBuiltin(GoTypes.Builtin.Rune);
                    default:
                        return GoType.Unknown;
                }
            }

            @Override
            public GoType visitConstant(GoTypeConstant constant) {
                if (constant.getType() != GoType.Unknown)
                    return constant.getType().accept(this);

                switch (constant.getKind()) {
                    case String:
                        return types().getBuiltin(GoTypes.Builtin.Rune);
                    default:
                        return GoType.Unknown;
                }
            }
        });

        return new GoType[]{GoTypes.makeSlice(getProject(), elementType)};
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
}
