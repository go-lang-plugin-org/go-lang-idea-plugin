package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public abstract class GoBinaryExpressionImpl<Op extends Enum<Op>> extends GoExpressionBase
        implements GoBinaryExpression<Op> {

    GoBinaryExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    protected IElementType getOperator(TokenSet tokenSet) {
        PsiElement child = findChildByFilter(tokenSet);
        return child != null ? child.getNode().getElementType() : null;
    }

    @Override
    @Nullable
    public GoExpr getLeftOperand() {
        GoExpr[] children = findChildrenByClass(GoExpr.class);
        return children.length == 0 ? null : children[0];
    }

    @Override
    public GoExpr getRightOperand() {
        GoExpr[] children = findChildrenByClass(GoExpr.class);
        return children.length <= 1 ? null : children[1];
    }

    @Override
    protected GoType[] resolveTypes() {
        GoExpr leftOperand = getLeftOperand();
        GoExpr rightOperand = getRightOperand();

        if (leftOperand == null && rightOperand == null)
            return GoType.EMPTY_ARRAY;

        if (leftOperand == null)
            return rightOperand.getType();

        if (rightOperand == null)
            return leftOperand.getType();

        GoType[] leftTypes = leftOperand.getType();
        GoType[] rightTypes = rightOperand.getType();

        if (leftTypes.length != 1 || rightTypes.length != 1 || leftTypes[0] == null || rightTypes[0] == null )
            return GoType.EMPTY_ARRAY;

        GoType leftType = leftTypes[0];
        GoType rightType = rightTypes[0];

        if ( leftType instanceof GoTypeConstant && rightType instanceof GoTypeConstant) {
            GoType type = computeConstant((GoTypeConstant) leftType, (GoTypeConstant) rightType);
            return type != null ? new  GoType[]{type} : GoType.EMPTY_ARRAY;
        }

        // old behaviour
        return leftType instanceof GoTypeConstant ? rightTypes : leftTypes;

//                // based on http://golang.org/ref/spec#Constant_expressions
//                if (leftOperand.isConstantExpression() && rightOperand.isConstantExpression()){
//                    String operator = getOperator().toString();
//                    boolean equality = operator.equals("!=") || operator.equals("==");
//                    boolean shift = operator.equals("<<")||operator.equals(">>");
//                    GoType leftType = leftTypes[0];
//                    GoType rightType = rightTypes[0];
//                    if (!equality) {
//                        if (shift){
//                            // shift operation returns untyped int
//                            GoNamesCache namesCache =
//                                    GoNamesCache.getInstance(this.getProject());
//                            return new GoType[]{
//                                    types().getBuiltin(GoTypes.Builtin.Int)
//                            };
//                        } else {
//                            if (leftType instanceof GoTypePointer && rightType instanceof GoTypePointer){
//                                GoTypePointer lptr = (GoTypePointer)leftType;
//                                GoTypePointer rptr = (GoTypePointer)rightType;
//                                leftType = lptr.getTargetType();
//                                rightType = rptr.getTargetType();
//                            }
//                            if (leftType instanceof GoTypeName && rightType instanceof GoTypeName) {
//                                String leftName = ((GoTypeName)leftType).getName();
//                                String rightName = ((GoTypeName)rightType).getName();
//                                // the right order is complex, float, rune, int
//                                if (leftName.startsWith("complex")){
//                                    return leftTypes;
//                                }
//                                if (rightName.startsWith("complex")){
//                                    return rightTypes;
//                                }
//                                if (leftName.startsWith("float")){
//                                    return leftTypes;
//                                }
//                                if (rightName.startsWith("float")){
//                                    return rightTypes;
//                                }
//                                if (leftName.startsWith("rune")) {
//                                    return leftTypes;
//                                }
//                                if (rightName.startsWith("rune")) {
//                                    return rightTypes;
//                                }
//                            }
//                        }
//                    }
//                }
            }

    protected abstract GoType computeConstant(GoTypeConstant left, GoTypeConstant right);

    @Override
    public boolean isConstantExpression() {
        GoType[] type = getType();
        return type.length == 1 && type[0] instanceof GoTypeConstant;
    }
}
