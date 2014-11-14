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
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFloat;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
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

    public static boolean checkParametersExp(GoPsiType psiType, GoExpr expr) {
        GoType type = GoTypes.fromPsi(psiType);

        GoPsiType resolved = resolveToFinalType(psiType);
        if (resolved instanceof GoPsiTypeInterface)
            return true;

        PsiElement firstChildOfExp = expr.getFirstChild();
        if (checkIsInterface(resolved)) {
            return true;
        }
        if (resolved == null) {
            return false;
        }


        GoType[] goTypes = expr.getType();
        if (goTypes.length != 0 && goTypes[0] != null) {
            return GoUtil.areTypesAssignable(GoTypes.getInstance(expr.getProject()).fromPsiType(psiType), goTypes[0]);
        }

        //Fix issue #520 with nil
        if (IsNil(expr)) {
            if (resolved instanceof GoPsiTypeName) {
                GoPsiTypeName typeName = (GoPsiTypeName) resolved;
                if (typeName.isPrimitive() && typeName.getName() != null && typeName.getName().equals("error")) {
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
                  }

        goTypes = expr.getType();
        if (goTypes.length != 0 && goTypes[0] != null) {
            return GoUtil.CompareTypes(psiType, goTypes[0], expr);
        }

        if (psiType instanceof GoPsiTypeFunction)
            return GoUtil.CompareTypes(psiType, null, expr);

        if (firstChildOfExp instanceof GoLiteralIdentifier) {
            GoPsiElement goPsiElement = GoUtil.ResolveTypeOfVarDecl((GoPsiElement) firstChildOfExp);
            if (goPsiElement instanceof GoPsiType)
                return GoUtil.CompareTypes(psiType, goPsiElement);
        }
        if (expr instanceof GoCallOrConvExpression && firstChildOfExp instanceof GoPsiTypeParenthesized) {
            return GoUtil.CompareTypes(psiType, ((GoPsiTypeParenthesized) firstChildOfExp).getInnerType(), expr);
        }
        psiType = resolved;
        if (psiType == null) {
            return false;
        }

        String typeText = psiType.getText();
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

        if (call instanceof GoBuiltinCallOrConversionExpression) {
            GoPsiType[] builtinTypes = ((GoBuiltinCallOrConversionExpression) call).getArgumentsType();
            if (builtinTypes.length > 0 && goExprs.length == builtinTypes.length) {
                for (; index < goExprs.length; index++) {
                    GoExpr goExpr = goExprs[index];
                    GoPsiType type = builtinTypes[index];
                    if (!checkParametersExp(type, goExpr)){
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", type.getText()),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, GoTypes.fromPsi(type)));
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
                if (call.isCallWithVariadicParameter()){
                    GoExpr goExpr = goExprs[index];
                    GoType[] types = goExpr.getType();
                    if (types.length!=1){
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", "[]"+typeName),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        return;
                    }
                    GoType exprType = types[0].underlyingType();
                    if (!(exprType instanceof GoTypeSlice)){
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", "[]"+typeName),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        return;
                    }

                    //TODO test with assignable
                    if (!((GoTypeSlice) exprType).getElementType().isIdentical(GoTypes.fromPsi(type)) ) {
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", "[]"+typeName),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        return;
                    }
                }else {
                    for (; index < goExprs.length; index++) {
                        GoExpr goExpr = goExprs[index];
                        if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                            result.addProblem(
                                    goExpr,
                                    GoBundle.message("warning.functioncall.type.mismatch", typeName),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, GoTypes.fromPsi(type)));
                            return;
                        }
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
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, GoTypes.fromPsi(type)));
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
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(goExpr, GoTypes.fromPsi(type)));
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

        GoFunctionDeclaration containingFunction = GoPsiUtils.findParentOfType(statement, GoFunctionDeclaration.class);
        if (containingFunction == null)
            return true;

        GoType[] returnTypes = containingFunction.getReturnTypes();

        GoType[] valueTypes = GoType.EMPTY_ARRAY;

        GoExpr[] expressions = statement.getExpressions();
        if (expressions.length == 1 && expressions[0] instanceof GoCallOrConvExpression) {
            GoFunctionDeclaration calledFunction = GoExpressionUtils.resolveToFunctionDeclaration(expressions[0]);
            if ( calledFunction != null )
                valueTypes = calledFunction.getReturnTypes();
        }

        if ( valueTypes.length != returnTypes.length )
            result.addProblem(
                    statement,
                    "Not Enough");

        //                for (GoType returnType : calledFunction.getReturnTypes()) {
//                    if (!returnType.isAssignableFrom(returnTypes[index])) {
//                        result.addProblem(
//                                expressions[0],
//                                GoBundle.message("warn.function.return.call.types.mismatch"),
//                                new ChangeReturnsParametersFix(statement));
//                        return false;
//                    }
//
//                    index++;
//                }
//            return true;

        return false;

//        for (GoFunctionParameter functionParameter : results) {
//            if (index >= expressions.length)
//                return false;
//            GoPsiType type = functionParameter.getType();
//            GoLiteralIdentifier[] identifiers = functionParameter.getIdentifiers();
//            String typeName = type != null ? type.getText() : "";
//            if (identifiers.length < 2) {
//                GoExpr goExpr = expressions[index];
//                if (!checkParametersExp(functionParameter.getType(), goExpr)) {
//                    result.addProblem(
//                            goExpr,
//                            GoBundle.message("warning.functioncall.type.mismatch", typeName),
//                            new CastTypeFix(goExpr, GoTypes.fromPsi(type)),
//                            new ChangeReturnsParametersFix(statement));
//                    return false;
//                }
//                index++;
//            } else {
//                for (GoLiteralIdentifier goLiteralIdentifier : identifiers) {
//                    GoExpr goExpr = expressions[index];
//                    if (!checkParametersExp(functionParameter.getType(), goExpr)) {
//                        result.addProblem(
//                                goExpr,
//                                GoBundle.message("warning.functioncall.type.mismatch", typeName),
//                                new CastTypeFix(goExpr, GoTypes.fromPsi(type)),
//                                new ChangeReturnsParametersFix(statement));
//
//                        return false;
//                    }
//                    index++;
//                }
//            }
//
//        }
//        return true;
    }
}

