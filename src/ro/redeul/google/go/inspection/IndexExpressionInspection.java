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

    private void checkIndexExpression(GoIndexExpression expression, final InspectionResult result) {
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

        GoType[] expressionTypes = expression.getBaseExpression().getType();
        if (expressionTypes.length != 1 || expressionTypes[0] == null)
            return;

        expressionTypes[0].underlyingType().accept(new UpdatingTypeVisitor<InspectionResult>() {
            @Override
            public void visitArray(GoTypeArray type, InspectionResult result, TypeVisitor<InspectionResult> visitor) {
                checkUnsignedIntegerConstantInRange(indexExpr, indexType, type.getLength(), result);
            }

            @Override
            public void visitSlice(GoTypeSlice type, InspectionResult result, TypeVisitor<InspectionResult> visitor) {
                checkUnsignedIntegerConstantInRange(indexExpr, indexType, Integer.MAX_VALUE, result);
            }

            @Override
            public void visitPrimitive(GoTypePrimitive type, InspectionResult data, TypeVisitor<InspectionResult> visitor) {
                switch (type.getType()) {
                    case String:
                        checkUnsignedIntegerConstantInRange(indexExpr, indexType, Integer.MAX_VALUE, result);
                        break;
                    default:
                        result.addProblem(
                                indexExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", GoTypes.getRepresentation(indexType, goFile))
                        );
                }
            }

            @Override
            public void visitPointer(GoTypePointer type, InspectionResult data, TypeVisitor<InspectionResult> visitor) {
                type.getTargetType().underlyingType().accept(visitor);
            }

            @Override
            public void visitConstant(GoTypeConstant type, InspectionResult data, TypeVisitor<InspectionResult> visitor) {
                switch (type.getKind()) {
                    case String:
                        String stringValue = type.getValueAs(String.class);
                        checkUnsignedIntegerConstantInRange(indexExpr, indexType, stringValue != null ? stringValue.length() : Integer.MAX_VALUE, result);
                    default:
                        result.addProblem(
                                indexExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", GoTypes.getRepresentation(indexType, goFile))
                        );
                }
            }

            @Override
            public void visitMap(GoTypeMap type, InspectionResult data, TypeVisitor<InspectionResult> visitor) {
                GoType keyType = type.getKeyType();
                if (!keyType.isAssignableFrom(indexType)) {
                    result.addProblem(
                            indexExpr,
                            GoBundle.message("warn.index.map.invalid.type",
                                    indexExpr.getText(),
                                    GoTypes.getRepresentation(indexType, goFile),
                                    GoTypes.getRepresentation(keyType, goFile)),
                            new CastTypeFix(indexExpr, keyType));
                }
            }
        }, result);
    }

    private void checkUnsignedIntegerConstantInRange(GoExpr indexExpr, GoType indexType, int length, InspectionResult result) {
        BigInteger integer = getIntegerConstant(indexType);

        if (!(indexType instanceof GoTypeConstant) || integer == null)
            result.addProblem(
                    indexExpr,
                    GoBundle.message("warning.functioncall.type.mismatch", "int"));

        if (integer != null && integer.compareTo(BigInteger.ZERO) < 0)
            result.addProblem(
                    indexExpr,
                    GoBundle.message("warning.index.invalid", integer.longValue(), "(index must be non-negative)"));

        if (integer != null && integer.compareTo(BigInteger.valueOf(length)) > 0)
            result.addProblem(
                    indexExpr,
                    GoBundle.message("warning.index.invalid", integer.longValue(), "(index out of bounds)"));
    }

    private BigInteger getIntegerConstant(GoType indexType) {
        if (!(indexType instanceof GoTypeConstant))
            return null;

        GoTypeConstant constant = (GoTypeConstant) indexType;
        BigDecimal decimal = null;
        BigInteger integer = null;

        if (constant.getKind() == GoTypeConstant.Kind.Complex) {
            GoNumber goNumber = constant.getValueAs(GoNumber.class);
            if (goNumber == null || goNumber.isComplex())
                return null;

            decimal = goNumber.getReal();
        }

        if (decimal != null || constant.getKind() == GoTypeConstant.Kind.Float) {
            decimal = (decimal == null) ? constant.getValueAs(BigDecimal.class) : decimal;
            if (decimal == null)
                return null;

            try {
                integer = decimal.toBigIntegerExact();
            } catch (ArithmeticException e) {
                return null;
            }
        }

        if (integer != null || constant.getKind() == GoTypeConstant.Kind.Integer) {
            integer = (integer == null) ? constant.getValueAs(BigInteger.class) : integer;
        }


        return integer;
    }
}
