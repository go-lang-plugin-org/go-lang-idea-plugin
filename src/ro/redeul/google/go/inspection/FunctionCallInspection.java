package ro.redeul.google.go.inspection;

import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.*;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoTypeInspectUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

import static ro.redeul.google.go.inspection.InspectionUtil.*;
import static ro.redeul.google.go.lang.psi.utils.GoExpressionUtils.getCallFunctionIdentifier;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getAs;
import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

public class FunctionCallInspection extends AbstractWholeGoFileInspection {
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitCallOrConvExpression(GoCallOrConvExpression expression) {
                super.visitCallOrConvExpression(expression);

                checkFunctionCallArguments(expression, result);
            }

            @Override
            public void visitBuiltinCallExpression(GoBuiltinCallOrConversionExpression expression) {
                super.visitBuiltinCallExpression(expression);

                GoPrimaryExpression baseExpression = expression.getBaseExpression();
                String expressionText = baseExpression.getText();
                if (expressionText.equals("make")) {
                    checkMakeCall(expression, result);

                } else if (expressionText.equals("new")) {
                    checkNewCall(expression, result);

                } else {
                    checkFunctionCallArguments(expression, result);

                }
            }
        }.visitFile(file);
    }

    private static void checkNewCall(GoBuiltinCallOrConversionExpression expression, InspectionResult result) {
        GoExpr[] arguments = expression.getArguments();
        GoPsiType type = expression.getTypeArgument();
        if (type == null) {
            if (arguments.length == 0) {
                result.addProblem(expression, GoBundle.message("error.missing.argument", "type", "new"));
            } else {
                result.addProblem(expression, GoBundle.message("error.expression.is.not.a.type", arguments[0].getText()));
            }
            return;
        }

        if (arguments.length != 0) {
            result.addProblem(expression, GoBundle.message("error.too.many.arguments.in.call", "new"));
        }
    }

    private static void checkMakeCall(GoBuiltinCallOrConversionExpression expression, InspectionResult result) {
        GoExpr[] arguments = expression.getArguments();
        GoPsiType type = expression.getTypeArgument();
        if (type == null) {
            result.addProblem(expression, GoBundle.message("error.incorrect.make.type"));
            return;
        }

        GoPsiType finalType = resolveToFinalType(type);
        if (finalType instanceof GoPsiTypeSlice) {
            checkMakeSliceCall(expression, arguments, result);
        } else if (finalType instanceof GoPsiTypeChannel) {
            checkMakeChannelCall(arguments, result);
        } else if (finalType instanceof GoPsiTypeMap) {
            checkMakeMapCall(arguments, result);
        } else {
            result.addProblem(expression, GoBundle.message("error.cannot.make.type", type.getText()));
        }
    }

    private static void checkMakeSliceCall(GoBuiltinCallOrConversionExpression expression,
                                           GoExpr[] arguments, InspectionResult result) {
        if (arguments.length > 2) {
            result.addProblem(arguments[2], arguments[arguments.length - 1],
                    GoBundle.message("error.too.many.arguments.in.call", "make"));
        } else if (arguments.length == 0) {
            String method = "make(" + expression.getTypeArgument().getText() + ")";
            result.addProblem(expression, GoBundle.message("error.missing.argument", "len", method));
        }
    }

    private static void checkMakeMapCall(GoExpr[] arguments, InspectionResult result) {
        if (arguments.length > 1) {
            result.addProblem(arguments[1], arguments[arguments.length - 1],
                    GoBundle.message("error.too.many.arguments.in.call", "make"));
        }
    }

    private static void checkMakeChannelCall(GoExpr[] arguments, InspectionResult result) {
        if (arguments.length > 1) {
            result.addProblem(arguments[1], arguments[arguments.length - 1],
                    GoBundle.message("error.too.many.arguments.in.call", "make"));
        }
    }

    static class ExpressionEvaluatorVisitor extends GoElementVisitorWithData<Pair<GoLiteral.Type, ? extends Number>> {
        @Override
        public void visitLiteralExpression(GoLiteralExpression expression) {
            GoLiteral literal = expression.getLiteral();
            switch (literal.getType()) {
                case Int:
                case Float:
                case Char:
                case Identifier:
                    literal.accept(this);
            }
        }

        @Override
        public void visitLiteralInteger(GoLiteral<BigInteger> literal) {
            setData(Pair.create(GoLiteral.Type.Int, literal.getValue()));
        }

        @Override
        public void visitLiteralFloat(GoLiteral<BigDecimal> literal) {
            setData(Pair.create(GoLiteral.Type.Float, literal.getValue()));
        }

        @Override
        public void visitLiteralChar(GoLiteral<Character> literal) {
            setData(Pair.create(GoLiteral.Type.Char, BigInteger.valueOf(literal.getValue())));
        }

        @Override
        public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
            if (identifier.isIota())
                setData(Pair.create(GoLiteral.Type.Int, identifier.getIotaValue()));
            else {
                GoLiteralIdentifier definition = GoPsiUtils.resolveSafely(identifier, GoLiteralIdentifier.class);
                if (definition != null) {
                    GoConstDeclaration constDeclaration = getAs(GoConstDeclaration.class, definition.getParent());

                    GoExpr valueExpression = constDeclaration.getExpression(definition);
                    if (valueExpression != null)
                        valueExpression.accept(this);
                }
            }
        }

        @Override
        public void visitParenthesisedExpression(GoParenthesisedExpression expression) {
            GoExpr inner = expression.getInnerExpression();
            if (inner != null)
                inner.accept(this);
        }

        @Override
        public void visitUnaryExpression(GoUnaryExpression expression) {
            GoExpr inner = expression.getExpression();
            if (inner == null)
                return;

            inner.accept(this);
            if (getData() == null)
                return;

            GoUnaryExpression.Op op = expression.getUnaryOp();

            switch (getData().first) {
                case Int:
                case Char:
                    BigInteger intValue = (BigInteger) getData().second;
                    switch (op) {
                        case Minus:
                            setData(Pair.create(GoLiteral.Type.Int, intValue.negate()));
                            break;
                        case Xor:
                            BigInteger twoComplementValue = intValue.signum() == -1
                                    ? BigInteger.ONE.negate().xor(intValue)
                                    : BigInteger.ONE.shiftLeft(intValue.bitLength()).subtract(BigInteger.ONE).xor(intValue);

                            setData(Pair.create(getData().first, twoComplementValue));
                            break;
                        case Plus:
                            // passthrough
                    }
                    break;
                case Float:
                    BigDecimal floatValue = (BigDecimal) getData().second;
                    switch (op) {
                        case Minus:
                            setData(Pair.create(GoLiteral.Type.Float, floatValue.negate()));
                            break;
                        case Xor:
                            setData(null);
                            break;
                        case Plus:
                            // passthrough
                    }
            }
        }

        @Override
        public void visitBinaryExpression(GoBinaryExpression expression) {
            GoExpr lExpr = expression.getLeftOperand();
            GoExpr rExpr = expression.getRightOperand();

            if (lExpr == null || rExpr == null)
                return;

            lExpr.accept(this);
            if (getData() == null)
                return;

//            Pair<GoLiteral.Type, ? extends Number> lValue = getData();
//            rExpr.accept(this);
//            if (getData() == null)
//                return;
//
//            Pair<GoLiteral.Type, ? extends Number> rValue = getData();

            setData(null);

//            BigDecimal lFloat = asFloatConstant(lValue.getSecond());
//            BigDecimal rFloat = asFloatConstant(rValue.getSecond());
//
//            BigInteger lInt = asIntegralConstant(lValue.getSecond());
//            BigInteger rInt = asIntegralConstant(rValue.getSecond());
//
//            GoBinaryExpression.Op op = expression.op();
//            if (lValue.first == GoLiteral.Type.Float || rValue.first == GoLiteral.Type.Float) {
//
//                switch (op) {
//                    case Plus:
//                        setData(Pair.create(GoLiteral.Type.Float, lFloat.add(rFloat)));
//                        break;
//                    case Minus:
//                        setData(Pair.create(GoLiteral.Type.Float, lFloat.subtract(rFloat)));
//                        break;
//                    case Mul:
//                        setData(Pair.create(GoLiteral.Type.Float, lFloat.multiply(rFloat)));
//                        break;
//                    case Quotient:
//                        if (rFloat.compareTo(BigDecimal.ZERO) != 0)
//                            setData(Pair.create(GoLiteral.Type.Float, lFloat.divide(rFloat)));
//                        break;
//                    case ShiftLeft:
//                        if ( lInt != null && rInt != null)
//                            setData(Pair.create(GoLiteral.Type.Int, lInt.shiftLeft(rInt.intValue())));
//                        break;
//                    case ShiftRight:
//                        if ( lInt != null && rInt != null)
//                            setData(Pair.create(GoLiteral.Type.Int, lInt.shiftRight(rInt.intValue())));
//                        break;
//
//                }
//                return;
//            }
//
//            if ( lInt == null || rInt == null )
//                return;
//
//            switch (op) {
//                case Plus:
//                    setData(Pair.create(GoLiteral.Type.Int, lInt.add(rInt)));
//                    break;
//                case Minus:
//                    setData(Pair.create(GoLiteral.Type.Int, lInt.subtract(rInt)));
//                    break;
//                case Mul:
//                    setData(Pair.create(GoLiteral.Type.Int, lInt.multiply(rInt)));
//                    break;
//                case Quotient:
//                    if (rInt.compareTo(BigInteger.ZERO) != 0)
//                        setData(Pair.create(GoLiteral.Type.Int, lInt.divide(rInt)));
//                    break;
//                case ShiftLeft:
//                    setData(Pair.create(GoLiteral.Type.Int, lInt.shiftLeft(rInt.intValue())));
//                    break;
//                case ShiftRight:
//                    setData(Pair.create(GoLiteral.Type.Int, lInt.shiftRight(rInt.intValue())));
//                    break;
//            }
        }
    }

    public static Pair<GoLiteral.Type, ? extends Number> evaluateConstantExpression(GoExpr expr) {
        if ( expr == null )
            return null;

        return expr.accept(new ExpressionEvaluatorVisitor());
    }

    @Nullable
    public static Number getNumberValueFromLiteralExpr(GoExpr expr) {
        GoType[] expressionType = expr.getType();
        if ( expressionType.length == 1 && expressionType[0] instanceof GoTypeConstant) {
            GoTypeConstant typeConstant = (GoTypeConstant) expressionType[0];
            switch (typeConstant.getKind()) {
                case Rune:
                    Character value = typeConstant.getValueAs(Character.class);
                    return value != null ? BigInteger.valueOf(value) : null;
            }

            return typeConstant.getValueAs(Number.class);
        }

        return null;
    }

    private static BigDecimal asFloatConstant(Number number) {
        if ( number instanceof BigDecimal )
            return (BigDecimal) number;

        return new BigDecimal((BigInteger) number);
    }

    private static Number integralIfPossible(BigDecimal decimal) {
        try {
            return decimal.toBigIntegerExact();
        } catch (ArithmeticException e) {
            return decimal;
        }
    }

    @Nullable
    private static BigInteger asIntegralConstant(Number number) {
        if (number instanceof BigInteger)
            return (BigInteger) number;

        try {
            return ((BigDecimal) number).toBigIntegerExact();
        } catch (ArithmeticException ex) {
            return null;
        }
    }

    private static void checkFunctionCallArguments(GoCallOrConvExpression call, InspectionResult result) {
        if (call == null) {
            return;
        }

        GoExpr[] arguments = call.getArguments();
        if (arguments == null) {
            return;
        }

//        if (arguments.length > 1) {
//            checkExpressionShouldReturnOneResult(arguments, result);
//        }

        int argumentCount = arguments.length;
        if (argumentCount == 1) {
            argumentCount = getExpressionResultCount(arguments[0]);
        }

        int expectedCount = getFunctionParameterCount(call);
        if (argumentCount == UNKNOWN_COUNT || expectedCount == UNKNOWN_COUNT) {
            return;
        }

        String name = "";
        GoPsiElement id = getCallFunctionIdentifier(call);
        if (id != null) {
            name = id.getText();
        }

        if (expectedCount == VARIADIC_COUNT) {
            GoTypeInspectUtil.checkFunctionTypeArguments(call, result);
        } else {
            if (argumentCount < expectedCount) {
                result.addProblem(call, GoBundle.message("error.not.enough.arguments.in.call", name));
            } else if (argumentCount > expectedCount) {
                result.addProblem(call, GoBundle.message("error.too.many.arguments.in.call", name));
            } else {
                GoTypeInspectUtil.checkFunctionTypeArguments(call, result);
            }
        }
    }
}
