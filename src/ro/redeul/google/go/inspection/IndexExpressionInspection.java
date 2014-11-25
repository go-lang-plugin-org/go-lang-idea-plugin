package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.CastTypeFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIndexExpression;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;

public class IndexExpressionInspection extends AbstractWholeGoFileInspection {
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitIndexExpression(GoIndexExpression expression) {
                checkIndexExpression(expression, result);
            }


        }.visitFile(file);
    }

    private void checkIndexExpression(final GoIndexExpression expression, final InspectionResult result) {
        final GoExpr indexExpr = expression.getIndex();
        if (indexExpr == null)
            return;

        final GoFile goFile = getAs(GoFile.class, indexExpr.getContainingFile());
        if (goFile == null)
            return;

        final GoType[] indexTypes = indexExpr.getType();

        if (indexTypes.length != 1 || indexTypes[0] == null)
            return;
        final GoType indexType = indexTypes[0];

        final GoType[] expressionTypes = expression.getBaseExpression().getType();
        if (expressionTypes.length != 1 || expressionTypes[0] == null)
            return;

        expressionTypes[0].underlyingType().accept(new UpdatingTypeVisitor<InspectionResult>() {
            @Override
            public void visitPointer(GoTypePointer type, InspectionResult data, TypeVisitor<InspectionResult> visitor) {
                type.getTargetType().underlyingType().accept(visitor);
            }

            @Override
            public void visitArray(GoTypeArray type, InspectionResult result, TypeVisitor<InspectionResult> visitor) {
                checkUnsignedIntegerConstantInRange(indexExpr, indexType, type.getLength(), "array", result);
            }

            @Override
            public void visitSlice(GoTypeSlice type, InspectionResult result, TypeVisitor<InspectionResult> visitor) {
                checkUnsignedIntegerConstantInRange(indexExpr, indexType, Integer.MAX_VALUE, "slice", result);
            }

            @Override
            public void visitMap(GoTypeMap type, InspectionResult data, TypeVisitor<InspectionResult> visitor) {
                GoType keyType = type.getKeyType();
                if (!keyType.isAssignableFrom(indexType)) {
                    result.addProblem(
                            indexExpr,
                            GoBundle.message("warn.index.invalid.key.type",
                                    indexExpr.getText(),
                                    GoTypes.getRepresentation(indexType, goFile),
                                    GoTypes.getRepresentation(keyType, goFile)),
                            new CastTypeFix(indexExpr, keyType));
                }
            }

            @Override
            public void visitPrimitive(GoTypePrimitive type, InspectionResult data, TypeVisitor<InspectionResult> visitor) {
                switch (type.getType()) {
                    case String:
                        checkUnsignedIntegerConstantInRange(indexExpr, indexType, Integer.MAX_VALUE, "string", result);
                        break;
                    default:
                        result.addProblem(
                                expression,
                                GoBundle.message(
                                        "warn.index.not.indexable.type",
                                        expression.getText(),
                                        GoTypes.getRepresentation(expressionTypes[0], goFile))
                        );
                }
            }

            @Override
            public void visitConstant(GoTypeConstant type, InspectionResult data, TypeVisitor<InspectionResult> visitor) {
                switch (type.kind()) {
                    case String:
                        String stringValue = type.getValueAs(String.class);
                        checkUnsignedIntegerConstantInRange(indexExpr, indexType, stringValue != null ? stringValue.length() : Integer.MAX_VALUE, "string", result);
                        break;
                    default:
                        result.addProblem(
                                expression,
                                GoBundle.message(
                                        "warn.index.not.indexable.type",
                                        expression.getText(),
                                        GoTypes.getRepresentation(expressionTypes[0], goFile))
                        );
                }
            }

        }, result);
    }

    private void checkUnsignedIntegerConstantInRange(GoExpr indexExpr, GoType indexType, int length, String type, InspectionResult result) {
        BigInteger value = getIntegerConstant(indexType);

        if (!(indexType instanceof GoTypeConstant) || value == null)
            result.addProblem(
                    indexExpr,
                    GoBundle.message("warn.index.invalid.index.type", type, indexExpr.getText()));

        if (value != null && value.compareTo(BigInteger.ZERO) < 0)
            result.addProblem(
                    indexExpr,
                    GoBundle.message("warn.index.invalid.index", type, indexExpr.getText(), value.toString(), "index must be non-negative"));

        if (value != null && value.compareTo(BigInteger.valueOf(length)) > 0)
            result.addProblem(
                    indexExpr,
                    GoBundle.message("warn.index.invalid.index", type, indexExpr.getText(), value.toString(), "out of bounds"));
    }

    private BigInteger getIntegerConstant(GoType indexType) {
        if (!(indexType instanceof GoTypeConstant))
            return null;

        GoTypeConstant constant = (GoTypeConstant) indexType;
        BigDecimal decimal = null;
        BigInteger integer = null;

        if (constant.kind() == GoTypeConstant.Kind.Complex) {
            GoNumber goNumber = constant.getValueAs(GoNumber.class);
            if (goNumber == null || goNumber.isComplex())
                return null;

            decimal = goNumber.getReal();
        }

        if (decimal != null || constant.kind() == GoTypeConstant.Kind.Float) {
            decimal = (decimal == null) ? constant.getValueAs(BigDecimal.class) : decimal;
            if (decimal == null)
                return null;

            try {
                integer = decimal.toBigIntegerExact();
            } catch (ArithmeticException e) {
                return null;
            }
        }

        if (integer != null || constant.kind() == GoTypeConstant.Kind.Integer) {
            integer = (integer == null) ? constant.getValueAs(BigInteger.class) : integer;
        }


        return integer;
    }
}
