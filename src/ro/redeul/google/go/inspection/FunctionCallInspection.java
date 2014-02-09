package ro.redeul.google.go.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFloat;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoTypeInspectUtil;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.inspection.InspectionUtil.*;
import static ro.redeul.google.go.lang.psi.utils.GoExpressionUtils.getCallFunctionIdentifier;
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
            public void visitBuiltinCallExpression(GoBuiltinCallExpression expression) {
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

    private static void checkNewCall(GoBuiltinCallExpression expression, InspectionResult result) {
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

    private static void checkMakeCall(GoBuiltinCallExpression expression, InspectionResult result) {
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

    private static void checkMakeSliceCall(GoBuiltinCallExpression expression,
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

    public static Number getNumberValueFromLiteralExpr(GoExpr expr) {
        if (expr instanceof GoLiteralExpression) {
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoLiteralIdentifier) {
                if (((GoLiteralIdentifier) literal).isIota()) {
                    Integer iotaValue = ((GoLiteralIdentifier) literal).getIotaValue();
                    if (iotaValue != null)
                        return iotaValue;

                } else {
                    PsiElement goConstIdentifier = GoUtil.ResolveReferece(literal);
                    PsiElement goConstSpec = goConstIdentifier.getParent();
                    if (goConstSpec instanceof GoConstDeclaration) {
                        GoExpr goConstExpr = ((GoConstDeclaration) goConstSpec).getExpression((GoLiteralIdentifier) goConstIdentifier);
                        if (goConstExpr != null)
                            return getNumberValueFromLiteralExpr(goConstExpr);
                    }
                }
            }
            if (literal instanceof GoLiteralInteger) {
                return ((GoLiteralInteger) literal).getValue();
            }
            if (literal instanceof GoLiteralFloat) {
                return ((GoLiteralFloat) literal).getValue();
            }
            if (literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR) {
                return GoPsiUtils.getRuneValue(literal.getText());

            }

        }
        if (expr instanceof GoBinaryExpression) {
            GoExpr leftOp = ((GoBinaryExpression) expr).getLeftOperand();
            GoExpr rightOp = ((GoBinaryExpression) expr).getRightOperand();
            IElementType op = ((GoBinaryExpression) expr).getOperator();
            if (op == GoElementTypes.oPLUS || op == GoElementTypes.oMINUS
                    || op == GoElementTypes.oMUL || op == GoElementTypes.oQUOTIENT
                    || op == GoElementTypes.oSHIFT_LEFT || op == GoElementTypes.oSHIFT_RIGHT) {
                Number leftVal = getNumberValueFromLiteralExpr(leftOp);
                if (leftVal != null) {
                    Number rightVal = getNumberValueFromLiteralExpr(rightOp);
                    if (rightVal != null) {
                        if (leftVal instanceof Integer && rightVal instanceof Integer) {
                            Integer left = leftVal.intValue();
                            Integer right = rightVal.intValue();
                            if (op == GoElementTypes.oPLUS)
                                return left + right;
                            if (op == GoElementTypes.oMINUS)
                                return left - right;
                            if (op == GoElementTypes.oMUL)
                                return left * right;
                            if (op == GoElementTypes.oQUOTIENT && right != 0)
                                return left / right;
                        } else {
                            Float left = leftVal.floatValue();
                            Float right = rightVal.floatValue();
                            if (op == GoElementTypes.oPLUS)
                                return left + right;
                            if (op == GoElementTypes.oMINUS)
                                return left - right;
                            if (op == GoElementTypes.oMUL)
                                return left * right;
                            if (op == GoElementTypes.oQUOTIENT && right != 0)
                                return left / right;
                        }
                        if ((leftVal instanceof Integer || (leftVal.intValue() == leftVal.floatValue()))
                                && (rightVal instanceof Integer || (rightVal.intValue() == rightVal.floatValue()))) {
                            if (op == GoElementTypes.oSHIFT_LEFT)
                                return leftVal.intValue() << rightVal.intValue();
                            if (op == GoElementTypes.oSHIFT_RIGHT)
                                return leftVal.intValue() >> rightVal.intValue();
                        }
                    }
                }
            }
        }
        if (expr instanceof GoUnaryExpression) {
            GoUnaryExpression.Op unaryOp = ((GoUnaryExpression) expr).getUnaryOp();
            GoExpr unaryExpr = ((GoUnaryExpression) expr).getExpression();
            if (unaryOp == GoUnaryExpression.Op.None || unaryOp == GoUnaryExpression.Op.Plus
                    || unaryOp == GoUnaryExpression.Op.Minus || unaryOp == GoUnaryExpression.Op.Xor) {
                Number unaryVal = getNumberValueFromLiteralExpr(unaryExpr);
                if (unaryVal != null) {
                    if (unaryOp == GoUnaryExpression.Op.Minus) {
                        if (unaryVal instanceof Integer)
                            return -((Integer) unaryVal);
                        if (unaryVal instanceof Float)
                            return -((Float) unaryVal);
                    }
                    if (unaryOp == GoUnaryExpression.Op.Xor) {
                        if (unaryVal instanceof Integer)
                            unaryVal = ~((Integer) unaryVal);
                        else
                            return null;
                    }
                }
                return unaryVal;
            }
        }
        if (expr instanceof GoParenthesisedExpression)
            return getNumberValueFromLiteralExpr(((GoParenthesisedExpression) expr).getInnerExpression());
        return null;
    }


    private static void checkFunctionCallArguments(GoCallOrConvExpression call, InspectionResult result) {
        if (call == null) {
            return;
        }

        GoExpr[] arguments = call.getArguments();
        if (arguments == null) {
            return;
        }

        if (arguments.length > 1) {
            checkExpressionShouldReturnOneResult(arguments, result);
        }

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