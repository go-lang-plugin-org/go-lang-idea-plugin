package ro.redeul.google.go.inspection;

<<<<<<< HEAD
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
=======
>>>>>>> Add inspection on return stmt
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
<<<<<<< HEAD
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoExpressionUtils;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
=======
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
>>>>>>> Add inspection on return stmt
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoTypeInspectUtil;

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

<<<<<<< HEAD
    private static boolean checkIsInterface(GoPsiType psiType) {
        if (psiType instanceof GoPsiTypeInterface)
            return true;
        if (psiType instanceof GoPsiTypeSlice)
            return checkIsInterface(((GoPsiTypeSlice) psiType).getElementType());
        if (psiType instanceof GoPsiTypePointer)
            return checkIsInterface(((GoPsiTypePointer) psiType).getTargetType());
        if (psiType instanceof GoPsiTypeArray)
            return checkIsInterface(((GoPsiTypeArray) psiType).getElementType());
        if (psiType instanceof GoPsiTypeChannel)
            return checkIsInterface(((GoPsiTypeChannel) psiType).getElementType());
        return false;
    }

    private static boolean checkParametersExp(GoPsiType type, GoExpr expr) {

        GoPsiType resolved = resolveToFinalType(type);
        if (resolved instanceof GoPsiTypeInterface)
            return true;

        PsiElement firstChildOfExp = expr.getFirstChild();
        if (checkIsInterface(resolved)) {
            return true;
        }

        if (expr.isConstantExpression()) {
            if (resolved == null) {
                return false;
            }
            String resolvedTypeName = resolved.getText();
            if (resolvedTypeName.startsWith("int") || resolvedTypeName.startsWith("uint")
                    || resolvedTypeName.equals("byte") || resolvedTypeName.equals("rune")) {
                    Number numValue = getNumberValueFromLiteralExpr(expr);
                    if (numValue == null)
                        return checkValidLiteralIntExpr(expr);
                    if (numValue instanceof Integer || numValue.intValue() == numValue.floatValue()){
                        Integer value = numValue.intValue();
                        if (resolvedTypeName.equals("int8"))
                            return value >= -128 && value <= 127;
                        if (resolvedTypeName.equals("int16"))
                            return value >= -32768 && value <= 32767;
                        if (resolvedTypeName.equals("int32") || resolvedTypeName.equals("rune"))
                            return value >= -2147483648 && value <= 2147483647;
                        if (resolvedTypeName.equals("int64") || resolvedTypeName.equals("int"))
                            return true;

                        if (resolvedTypeName.equals("uint8") || resolvedTypeName.equals("byte"))
                            return value >= 0 && value <= 255;
                        if (resolvedTypeName.equals("uint16"))
                            return value >= 0 && value <= 65535;
                        if (resolvedTypeName.equals("uint32"))
                            return value >= 0;
                        if (resolvedTypeName.equals("uint64") || resolvedTypeName.equals("uint"))
                            return value >= 0;
                    } else {
                        return false;
                    }
            }
            if (resolvedTypeName.startsWith("float")) {
                return checkValidLiteralFloatExpr(expr);
            }
            if (firstChildOfExp instanceof GoLiteralString) {
                return resolvedTypeName.equals("string");
            }
            if (firstChildOfExp instanceof GoLiteralBool) {
                return resolvedTypeName.equals("bool");
            }
        }

        GoType[] goTypes = expr.getType();
        if (goTypes.length != 0 && goTypes[0] != null) {
            return GoUtil.CompairTypes(type, goTypes[0], expr);
        }

        if (type instanceof GoPsiTypeFunction)
            return GoUtil.CompairTypes(type, null, expr);

        if (firstChildOfExp instanceof GoLiteralIdentifier) {
            GoPsiElement goPsiElement = GoUtil.ResolveTypeOfVarDecl((GoPsiElement) firstChildOfExp);
            if (goPsiElement instanceof GoPsiType)
                return GoUtil.CompairTypes(type, goPsiElement);
        }
        if (expr instanceof GoCallOrConvExpression && firstChildOfExp instanceof GoPsiTypeParenthesized) {
            return GoUtil.CompairTypes(type, ((GoPsiTypeParenthesized) firstChildOfExp).getInnerType(), expr);
        }
        type = resolved;
        if (type == null) {
            return false;
        }

        String typeText = type.getText();
        if (expr instanceof GoLiteralExpression) {
            GoLiteral.Type type1 = ((GoLiteralExpression) expr).getLiteral().getType();
            return type1 == GoLiteral.Type.Identifier || type1.name().toLowerCase().equals(typeText);
        }

        return true;

    }

    private static boolean checkValidLiteralFloatExpr(GoExpr expr) {
        if (expr instanceof GoLiteralExpression) {
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoLiteralIdentifier) {
                //Never will be null
                PsiElement goPsiElement = GoUtil.ResolveReferece(literal).getParent();
                if (goPsiElement instanceof GoConstDeclaration) {
                    for (GoExpr goExpr : ((GoConstDeclaration) goPsiElement).getExpressions()) {
                        if (!checkValidLiteralFloatExpr(goExpr))
                            return false;
                    }
                }
                return true;
            }
            if (literal instanceof GoLiteralExpression)
                return checkValidLiteralIntExpr((GoExpr) literal);
            return literal instanceof GoLiteralFloat || literal instanceof GoLiteralInteger || literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR;
        }
        if (expr instanceof GoBinaryExpression) {
            if (!checkValidLiteralFloatExpr(((GoBinaryExpression) expr).getLeftOperand()))
                return false;
            return checkValidLiteralFloatExpr(((GoBinaryExpression) expr).getRightOperand());
        }
        if (expr instanceof GoUnaryExpression)
            return checkValidLiteralFloatExpr(((GoUnaryExpression) expr).getExpression());
        return false;
    }

    public static Number getNumberValueFromLiteralExpr(GoExpr expr) {
        if (expr instanceof GoLiteralExpression){
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoLiteralIdentifier){
                if (((GoLiteralIdentifier) literal).isIota()){
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
            if (literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR){
                return GoPsiUtils.getRuneValue(literal.getText());

            }

        }
        if (expr instanceof GoBinaryExpression){
            GoExpr leftOp = ((GoBinaryExpression) expr).getLeftOperand();
            GoExpr rightOp = ((GoBinaryExpression) expr).getRightOperand();
            IElementType op = ((GoBinaryExpression) expr).getOperator();
            if (op == GoElementTypes.oPLUS || op == GoElementTypes.oMINUS
                    || op == GoElementTypes.oMUL || op == GoElementTypes.oQUOTIENT
                    || op == GoElementTypes.oSHIFT_LEFT || op == GoElementTypes.oSHIFT_RIGHT){
                Number leftVal = getNumberValueFromLiteralExpr(leftOp);
                if (leftVal != null){
                    Number rightVal = getNumberValueFromLiteralExpr(rightOp);
                    if (rightVal != null){
                        if (leftVal instanceof Integer && rightVal instanceof Integer){
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
                                && (rightVal instanceof Integer || (rightVal.intValue() == rightVal.floatValue()))){
                            if (op == GoElementTypes.oSHIFT_LEFT)
                                return leftVal.intValue() << rightVal.intValue();
                            if (op == GoElementTypes.oSHIFT_RIGHT)
                                return leftVal.intValue() >> rightVal.intValue();
                        }
                    }
                }
            }
        }
        if (expr instanceof GoUnaryExpression){
            GoUnaryExpression.Op unaryOp = ((GoUnaryExpression) expr).getUnaryOp();
            GoExpr unaryExpr = ((GoUnaryExpression) expr).getExpression();
            if (unaryOp == GoUnaryExpression.Op.None || unaryOp == GoUnaryExpression.Op.Plus
                    || unaryOp == GoUnaryExpression.Op.Minus || unaryOp == GoUnaryExpression.Op.Xor) {
                Number unaryVal = getNumberValueFromLiteralExpr(unaryExpr);
                if (unaryVal != null) {
                    if (unaryOp == GoUnaryExpression.Op.Minus){
                        if (unaryVal instanceof Integer)
                            return - ((Integer) unaryVal);
                        if (unaryVal instanceof Float)
                            return -((Float) unaryVal);
                    }
                    if (unaryOp == GoUnaryExpression.Op.Xor){
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

    private static boolean checkValidLiteralIntExpr(GoExpr expr) {
        if (expr instanceof GoLiteralExpression) {
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoLiteralIdentifier) {
                //Never will be null
                PsiElement goPsiElement = GoUtil.ResolveReferece(literal).getParent();
                if (goPsiElement instanceof GoConstDeclaration) {
                    for (GoExpr goExpr : ((GoConstDeclaration) goPsiElement).getExpressions()) {
                        if (!checkValidLiteralIntExpr(goExpr))
                            return false;
                    }
                }
                return true;
            }
            if (literal instanceof GoLiteralExpression)
                return checkValidLiteralIntExpr((GoExpr) literal);
            if (literal instanceof GoLiteralInteger || literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR)
                return true;
            return literal instanceof GoLiteralFloat && literal.getText().matches("^[0-9]*\\.0*$");
        }
        if (expr instanceof GoBinaryExpression) {
            if (!checkValidLiteralIntExpr(((GoBinaryExpression) expr).getLeftOperand()))
                return false;
            return checkValidLiteralIntExpr(((GoBinaryExpression) expr).getRightOperand());
        }
        if (expr instanceof GoUnaryExpression)
            return checkValidLiteralIntExpr(((GoUnaryExpression) expr).getExpression());
        if (expr instanceof GoParenthesisedExpression)
            return checkValidLiteralIntExpr(((GoParenthesisedExpression) expr).getInnerExpression());
        return false;
    }


    private static void checkFunctionTypeArguments(GoCallOrConvExpression call, InspectionResult result) {
        GoFunctionDeclaration goFunctionDeclaration = GoExpressionUtils.resolveToFunctionDeclaration(call);
        GoExpr[] goExprs = call.getArguments();
        int index = 0;
        if (goFunctionDeclaration == null)
            return;
        for (GoFunctionParameter functionParameter : goFunctionDeclaration.getParameters()) {
            if (index >= goExprs.length)
                return;
            GoPsiType type = functionParameter.getType();
            if (functionParameter.isVariadic()) {
                GoExpr goExpr = goExprs[index];
                for (; index < goExprs.length; index++)
                    if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", type.getText()),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, type));
                        return;
                    }
            } else {
                GoLiteralIdentifier[] identifiers = functionParameter.getIdentifiers();
                if (identifiers.length < 2) {
                    GoExpr goExpr = goExprs[index];
                    if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", type.getText()),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, type));
                        return;
                    }
                    index++;
                } else {
                    for (GoLiteralIdentifier goLiteralIdentifier : identifiers) {
                        GoExpr goExpr = goExprs[index];
                        if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                            result.addProblem(
                                    goExpr,
                                    GoBundle.message("warning.functioncall.type.mismatch", type.getText()),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, type));
                            return;
                        }
                        index++;
                    }
                }
            }
        }

    }
=======
>>>>>>> Add inspection on return stmt

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

        if (argumentCount < expectedCount) {
            result.addProblem(call, GoBundle.message("error.not.enough.arguments.in.call", name));
        } else if (argumentCount > expectedCount) {
            result.addProblem(call, GoBundle.message("error.too.many.arguments.in.call", name));
        } else {
            GoTypeInspectUtil.checkFunctionTypeArguments(call, result);
        }
    }
}
