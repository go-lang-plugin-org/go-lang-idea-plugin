package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoMultiplicativeExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class TypeMatchInspection extends AbstractWholeGoFileInspection {

    @Override
    protected void doCheckFile(@NotNull final GoFile file, @NotNull final InspectionResult problems) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitAdditiveExpression(GoAdditiveExpression expression) {
                visitElement(expression);

                checkBinaryExpression(expression, problems, file);
            }

            @Override
            public void visitMultiplicativeExpression(GoMultiplicativeExpression expression) {
                visitElement(expression);

                checkBinaryExpression(expression, problems, file);
            }

            @Override
            public void visitRelationalExpression(GoRelationalExpression expression) {
                visitElement(expression);

                checkRelationalExpression(expression, problems, file);
            }
        }.visitFile(file);
    }

    private void checkBinaryExpression(final GoBinaryExpression<? extends GoBinaryExpression.BinaryOp> expression, final InspectionResult problems, GoFile file) {

        GoExpr rightOperand = expression.getRightOperand();
        GoExpr leftOperand = expression.getLeftOperand();

        if (!validateSingleValue(leftOperand, problems) || !(validateSingleValue(rightOperand, problems)))
            return;

        GoType leftType = GoTypes.get(leftOperand.getType());
        GoType rightType = GoTypes.get(rightOperand.getType());

        if (leftType.isIdentical(rightType)) {
            String wrongType = leftType.underlyingType().accept(new TypeVisitor<String>() {
                @Override
                public String visitArray(GoTypeArray type) { return "array"; }

                @Override
                public String visitPointer(GoTypePointer type) { return "pointer"; }

                @Override
                public String visitChannel(GoTypeChannel type) {
                    return "chan";
                }

                @Override
                public String visitFunction(GoTypeFunction type) {
                    return "function";
                }

                @Override
                public String visitMap(GoTypeMap type) {
                    return "map";
                }

                @Override
                public String visitSlice(GoTypeSlice type) { return "slice"; }

                @Override
                public String visitInterface(GoTypeInterface type) { return "interface"; }

                @Override
                public String visitPrimitive(GoTypePrimitive type) {
                    return type.implementsOp(expression.op()) ? null : type.getName();
                }
            });

            if (wrongType != null) {
                problems.addProblem(
                        expression,
                        GoBundle.message(
                                "warn.expr.op.not.defined",
                                expression.getText(),
                                expression.op().getText(),
                                wrongType));
            }
        } else {
            if ( leftType instanceof GoTypeConstant || rightType instanceof GoTypeConstant )
                return;

            problems.addProblem(
                    expression,
                    GoBundle.message(
                            "warn.expr.mispatched.types",
                            expression.getText(),
                            GoTypes.getRepresentation(leftType, file),
                            GoTypes.getRepresentation(rightType, file)));
        }
    }

    private boolean validateSingleValue(GoExpr expr, InspectionResult problems) {
        if (expr == null)
            return false;

        GoType types[] = expr.getType();
        if (types.length > 1) {
            problems.addProblem(expr, GoBundle.message("error.multiple.value.in.single.value.context", expr.getText()));
            return false;
        }

        return true;
    }

    private void checkRelationalExpression(GoRelationalExpression expression, InspectionResult problems, GoFile file) {

        GoExpr rightOperand = expression.getRightOperand();
        GoExpr leftOperand = expression.getLeftOperand();

        if (!validateSingleValue(leftOperand, problems) || !(validateSingleValue(rightOperand, problems)))
            return;

        GoType leftType = GoTypes.get(leftOperand.getType());
        GoType rightType = GoTypes.get(rightOperand.getType());

        GoType underlyingLeft = leftType.underlyingType();
        GoType underlyingRight = rightType.underlyingType();

        if (!underlyingLeft.isIdentical(underlyingRight)) {

            if ( underlyingLeft instanceof GoTypeConstant || underlyingRight instanceof GoTypeConstant )
                return;

            if ( leftType == GoType.Nil && (underlyingRight instanceof GoTypePointer || underlyingRight instanceof GoTypeSlice || underlyingRight instanceof GoTypeMap || underlyingRight instanceof GoTypeFunction || underlyingRight instanceof GoTypeInterface))
                return;

            if ( rightType == GoType.Nil && (underlyingLeft instanceof GoTypePointer || underlyingLeft instanceof GoTypeSlice || underlyingLeft instanceof GoTypeMap || underlyingLeft instanceof GoTypeFunction || underlyingLeft instanceof GoTypeInterface))
                return;

            problems.addProblem(
                    expression,
                    GoBundle.message(
                            "warn.expr.mispatched.types",
                            expression.getText(),
                            GoTypes.getRepresentation(leftType, file),
                            GoTypes.getRepresentation(rightType, file)));
        }
    }
}
