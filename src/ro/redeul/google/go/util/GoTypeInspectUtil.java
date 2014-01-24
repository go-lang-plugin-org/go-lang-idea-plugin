package ro.redeul.google.go.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.InspectionResult;
import ro.redeul.google.go.inspection.fix.CastTypeFix;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.*;
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
        if (psiType instanceof GoPsiTypeSlice)
            return checkIsInterface(((GoPsiTypeSlice) psiType).getElementType());
        if (psiType instanceof GoPsiTypePointer)
            return checkIsInterface(((GoPsiTypePointer) psiType).getTargetType());
        if (psiType instanceof GoPsiTypeArray)
            return checkIsInterface(((GoPsiTypeArray) psiType).getElementType());
        return psiType instanceof GoPsiTypeChannel && checkIsInterface(((GoPsiTypeChannel) psiType).getElementType());
    }

    public static boolean checkParametersExp(GoPsiType type, GoExpr expr) {

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
            if (resolvedTypeName.startsWith("int")) {
                return checkValidLiteralIntExpr(expr);
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
            return checkValidLiteralFloatExpr(((GoBinaryExpression) expr).getLeftOperand()) && checkValidLiteralFloatExpr(((GoBinaryExpression) expr).getRightOperand());
        }
        return expr instanceof GoUnaryExpression && checkValidLiteralFloatExpr(((GoUnaryExpression) expr).getExpression());
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
            return literal instanceof GoLiteralInteger || literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR || literal instanceof GoLiteralFloat && literal.getText().matches("^[0-9]*\\.0+$");
        }
        if (expr instanceof GoBinaryExpression) {
            return checkValidLiteralIntExpr(((GoBinaryExpression) expr).getLeftOperand()) && checkValidLiteralIntExpr(((GoBinaryExpression) expr).getRightOperand());
        }
        if (expr instanceof GoUnaryExpression)
            return checkValidLiteralIntExpr(((GoUnaryExpression) expr).getExpression());
        return expr instanceof GoParenthesisedExpression && checkValidLiteralIntExpr(((GoParenthesisedExpression) expr).getInnerExpression());
    }


    public static void checkFunctionTypeArguments(GoCallOrConvExpression call, InspectionResult result) {
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

    public static void checkFunctionTypeReturns(GoReturnStatement statement, InspectionResult result) {
        GoFunctionDeclaration goFunctionDeclaration = GoPsiUtils.findParentOfType(statement, GoFunctionDeclaration.class);
        GoExpr[] goExprs = statement.getExpressions();
        int index = 0;
        if (goFunctionDeclaration == null)
            return;

        for (GoFunctionParameter functionParameter : goFunctionDeclaration.getResults()) {
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
}
