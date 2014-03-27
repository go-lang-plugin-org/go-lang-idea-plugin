package ro.redeul.google.go.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.FunctionCallInspection;
import ro.redeul.google.go.inspection.InspectionResult;
import ro.redeul.google.go.inspection.fix.CastTypeFix;
import ro.redeul.google.go.inspection.fix.ChangeReturnsParametersFix;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.*;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoExpressionUtils;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

public class GoTypeInspectUtil {


    public static boolean checkIsInterface(GoPsiType psiType) {
        if (psiType instanceof GoPsiTypeInterface)
            return true;
        if (psiType instanceof GoPsiTypeName)
            return psiType.getName().equals("error") && ((GoPsiTypeName) psiType).isPrimitive();
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

    public static boolean checkParametersExp(GoPsiType type, GoExpr expr) {

        GoPsiType resolved = resolveToFinalType(type);
        if (resolved instanceof GoPsiTypeInterface)
            return true;

        PsiElement firstChildOfExp = expr.getFirstChild();
        if (checkIsInterface(resolved)) {
            return true;
        }
        if (resolved == null) {
            return false;
        }
        //Fix issue #520 with nil
        if (IsNil(expr)) {
            if (resolved instanceof GoPsiTypeName) {
                if (((GoPsiTypeName) resolved).isPrimitive() && resolved.getName().equals("error")) {
                    return true;
                }
            }
            return resolved instanceof GoPsiTypeInterface ||
                    resolved instanceof GoPsiTypeFunction ||
                    resolved instanceof GoPsiTypePointer ||
                    resolved instanceof GoPsiTypeSlice ||
                    resolved instanceof GoPsiTypeMap ||
                    resolved instanceof GoPsiTypeChannel;
        } else if (expr.isConstantExpression()) {
            String resolvedTypeName = resolved.getText();

            if (resolvedTypeName.startsWith("int") || resolvedTypeName.startsWith("uint")
                    || resolvedTypeName.equals("byte") || resolvedTypeName.equals("rune")) {
                Number numValue = FunctionCallInspection.getNumberValueFromLiteralExpr(expr);
                if (numValue == null)
                    return checkValidLiteralIntExpr(expr);
                if (numValue instanceof Integer || numValue.intValue() == numValue.floatValue()) {
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
            return GoUtil.CompareTypes(type, goTypes[0], expr);
        }

        if (type instanceof GoPsiTypeFunction)
            return GoUtil.CompareTypes(type, null, expr);

        if (firstChildOfExp instanceof GoLiteralIdentifier) {
            GoPsiElement goPsiElement = GoUtil.ResolveTypeOfVarDecl((GoPsiElement) firstChildOfExp);
            if (goPsiElement instanceof GoPsiType)
                return GoUtil.CompareTypes(type, goPsiElement);
        }
        if (expr instanceof GoCallOrConvExpression && firstChildOfExp instanceof GoPsiTypeParenthesized) {
            return GoUtil.CompareTypes(type, ((GoPsiTypeParenthesized) firstChildOfExp).getInnerType(), expr);
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

    public static boolean IsNil(PsiElement psiElement) {
        if (psiElement instanceof GoParenthesisedExpression)
            return IsNil(((GoParenthesisedExpression) psiElement).getInnerExpression());
        if (psiElement instanceof GoLiteralExpression)
            psiElement = ((GoLiteralExpression) psiElement).getLiteral();
        return psiElement instanceof GoLiteralIdentifier && ((GoLiteralIdentifier) psiElement).getName().equals("nil");
    }

    public static boolean checkValidLiteralFloatExpr(GoExpr expr) {
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

    public static boolean checkValidLiteralIntExpr(GoExpr expr) {
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

    public static void checkFunctionTypeArguments(GoCallOrConvExpression call, InspectionResult result) {
        GoFunctionDeclaration goFunctionDeclaration = GoExpressionUtils.resolveToFunctionDeclaration(call);
        GoExpr[] goExprs = call.getArguments();
        int index = 0;

        if (goFunctionDeclaration == null)
            return;

        if (call instanceof GoBuiltinCallExpression) {
            GoPsiType[] builtinTypes = ((GoBuiltinCallExpression) call).getArgumentsType();
            if (builtinTypes.length > 0 && goExprs.length == builtinTypes.length) {
                for (; index < goExprs.length; index++) {
                    GoExpr goExpr = goExprs[index];
                    GoPsiType type = builtinTypes[index];
                    if (!checkParametersExp(type, goExpr)){
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", type.getText()),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, type));
                        return;
                    }
                }
            }
            return;
        }

        for (GoFunctionParameter functionParameter : goFunctionDeclaration.getParameters()) {
            if (index >= goExprs.length)
                return;
            GoPsiType type = functionParameter.getType();
            String typeName = type != null ? type.getText() : "";
            if (functionParameter.isVariadic()) {
                for (; index < goExprs.length; index++) {
                    GoExpr goExpr = goExprs[index];
                    if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", typeName),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, type));
                        return;
                    }
                }
            } else {
                GoLiteralIdentifier[] identifiers = functionParameter.getIdentifiers();
                if (identifiers.length < 2) {
                    GoExpr goExpr = goExprs[index];
                    if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", typeName),
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
                                    GoBundle.message("warning.functioncall.type.mismatch", typeName),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, type));
                            return;
                        }
                        index++;
                    }
                }
            }
        }

    }


    public static boolean checkFunctionTypeReturns(GoReturnStatement statement, InspectionResult result) {
        int index = 0;

        GoFunctionDeclaration goFunctionDeclaration = GoPsiUtils.findParentOfType(statement, GoFunctionDeclaration.class);
        if (goFunctionDeclaration == null)
            return true;
        GoExpr[] goExprs = statement.getExpressions();

        GoFunctionParameter[] results = goFunctionDeclaration.getResults();
        if (goExprs.length == 1 && goExprs[0] instanceof GoCallOrConvExpression) {

            GoFunctionDeclaration goFunctionDeclarationResolved = GoExpressionUtils.resolveToFunctionDeclaration(goExprs[0]);
            for (GoFunctionParameter resolvedParameter : goFunctionDeclarationResolved.getResults()) {
                if (!results[index].getType().isIdentical(resolvedParameter.getType())) {
                    result.addProblem(
                            goExprs[0],
                            GoBundle.message("warning.function.return.call.types.mismatch"),
                            new ChangeReturnsParametersFix(statement));
                    return false;
                }
                index++;
            }
            return true;

        }

        for (GoFunctionParameter functionParameter : results) {
            if (index >= goExprs.length)
                return false;
            GoPsiType type = functionParameter.getType();
            GoLiteralIdentifier[] identifiers = functionParameter.getIdentifiers();
            String typeName = type != null ? type.getText() : "";
            if (identifiers.length < 2) {
                GoExpr goExpr = goExprs[index];
                if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                    result.addProblem(
                            goExpr,
                            GoBundle.message("warning.functioncall.type.mismatch", typeName),
                            new CastTypeFix(goExpr, type),
                            new ChangeReturnsParametersFix(statement));
                    return false;
                }
                index++;
            } else {
                for (GoLiteralIdentifier goLiteralIdentifier : identifiers) {
                    GoExpr goExpr = goExprs[index];
                    if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", typeName),
                                new CastTypeFix(goExpr, type),
                                new ChangeReturnsParametersFix(statement));

                        return false;
                    }
                    index++;
                }
            }

        }
        return true;
    }
}
