package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

public abstract class GoBinaryExpressionImpl extends GoExpressionBase
        implements GoBinaryExpression {

    GoBinaryExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    @Override
    public IElementType getOperator() {
        PsiElement child = findChildByFilter(GoElementTypes.BINARY_OPS);
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

        if (leftTypes.length == 1 && rightTypes.length == 1 && leftTypes[0] != null && rightTypes[0] != null) {
            if (leftTypes[0].isIdentical(rightTypes[0])) {
                return leftTypes;
            } else {
                // based on http://golang.org/ref/spec#Constant_expressions
                if (leftOperand.isConstantExpression() && rightOperand.isConstantExpression()){
                    String operator = getOperator().toString();
                    boolean equality = operator.equals("!=") || operator.equals("==");
                    boolean shift = operator.equals("<<")||operator.equals(">>");
                    GoType leftType = leftTypes[0];
                    GoType rightType = rightTypes[0];
                    GoUnderlyingType leftUnder = leftType.getUnderlyingType();
                    GoUnderlyingType rightUnder = rightType.getUnderlyingType();
                    if (!equality) {
                        if (shift){
                            // shift operation returns untyped int
                            GoNamesCache namesCache =
                                    GoNamesCache.getInstance(this.getProject());
                            return new GoType[]{
                                    GoTypes.getBuiltin(GoTypes.Builtin.Int, namesCache)
                            };
                        } else {
                            if (leftType instanceof GoTypePointer && rightType instanceof GoTypePointer){
                                GoTypePointer lptr = (GoTypePointer)leftType;
                                GoTypePointer rptr = (GoTypePointer)rightType;
                                leftType = lptr.getTargetType();
                                rightType = rptr.getTargetType();
                            }
                            if (leftType instanceof GoTypeName && rightType instanceof GoTypeName) {
                                String leftName = ((GoTypeName)leftType).getName();
                                String rightName = ((GoTypeName)rightType).getName();
                                // the right order is complex, float, rune, int
                                if (leftName.startsWith("complex")){
                                    return leftTypes;
                                }
                                if (rightName.startsWith("complex")){
                                    return rightTypes;
                                }
                                if (leftName.startsWith("float")){
                                    return leftTypes;
                                }
                                if (rightName.startsWith("float")){
                                    return rightTypes;
                                }
                                if (leftName.startsWith("rune")) {
                                    return leftTypes;
                                }
                                if (rightName.startsWith("rune")) {
                                    return rightTypes;
                                }
                            }
                        }
                    }
                }
                // old behaviour
                if (leftOperand.isConstantExpression()) {
                    return rightTypes;
                } else if (rightOperand.isConstantExpression()) {
                    return leftTypes;
                } else {
                    return leftTypes;
                }
            }
        }
        return GoType.EMPTY_ARRAY;
    }

    @Override
    public boolean isConstantExpression() {
        GoExpr leftOperand = getLeftOperand();
        GoExpr rightOperand = getRightOperand();

        return
                leftOperand != null && leftOperand.isConstantExpression() &&
                        rightOperand != null && rightOperand.isConstantExpression();
    }
}
