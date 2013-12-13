package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.CastTypeFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.*;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoExpressionUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
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
            return;
        } else if (arguments.length == 0) {
            String method = "make(" + expression.getTypeArgument().getText() + ")";
            result.addProblem(expression, GoBundle.message("error.missing.argument", "len", method));
            return;
        }

        // TODO: check len
        GoExpr len = arguments[0];

        if (arguments.length != 2) {
            return;
        }

        // TODO: check capacity
        GoExpr capacity = arguments[1];
    }

    private static void checkMakeMapCall(GoExpr[] arguments, InspectionResult result) {
        if (arguments.length > 1) {
            result.addProblem(arguments[1], arguments[arguments.length - 1],
                    GoBundle.message("error.too.many.arguments.in.call", "make"));
            return;
        }

        if (arguments.length != 1) {
            return;
        }

        // TODO: check space
        GoExpr space = arguments[0];
    }

    private static void checkMakeChannelCall(GoExpr[] arguments, InspectionResult result) {
        if (arguments.length > 1) {
            result.addProblem(arguments[1], arguments[arguments.length - 1],
                    GoBundle.message("error.too.many.arguments.in.call", "make"));
            return;
        }

        if (arguments.length != 1) {
            return;
        }

        // TODO: check bufferSize
        GoExpr bufferSize = arguments[0];
    }

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

    private static boolean checkParametersExp(GoPsiType type, GoExpr goExpr) {

        GoPsiType resolved = resolveToFinalType(type);
        if (resolved instanceof GoPsiTypeInterface)
            return true;

        PsiElement firstChildOfExp = goExpr.getFirstChild();
        if (checkIsInterface(resolved)) {
            return true;
        }

        if (firstChildOfExp instanceof GoLiteralInteger) {
            return resolved.getText().startsWith("int");
        }
        if (firstChildOfExp instanceof GoLiteralFloat) {
            return resolved.getText().startsWith("float");
        }
        if (firstChildOfExp instanceof GoLiteralString) {
            return resolved.getText().equals("string");
        }
        if (firstChildOfExp instanceof GoLiteralBool) {
            return resolved.getText().equals("bool");
        }

        GoType[] goTypes = goExpr.getType();
        if (goTypes.length != 0 && goTypes[0] != null) {
            return GoUtil.CompairTypes(type, goTypes[0], goExpr);
        }

        if (type instanceof GoPsiTypeFunction)
            return GoUtil.CompairTypes(type, null, goExpr);

        if (firstChildOfExp instanceof GoLiteralIdentifier) {
            GoPsiElement goPsiElement = GoUtil.ResolveTypeOfVarDecl((GoPsiElement) firstChildOfExp);
            if (goPsiElement instanceof GoPsiType)
                return GoUtil.CompairTypes(type, goPsiElement);
        }
        if (goExpr instanceof GoCallOrConvExpression && firstChildOfExp instanceof GoPsiTypeParenthesized) {
            return GoUtil.CompairTypes(type, ((GoPsiTypeParenthesized) firstChildOfExp).getInnerType(), goExpr);
        }
        type = resolved;
        String typeText = type.getText();
        if (goExpr instanceof GoLiteralExpression) {
            GoLiteral.Type type1 = ((GoLiteralExpression) goExpr).getLiteral().getType();
            return type1 == GoLiteral.Type.Identifier || type1.name().toLowerCase().equals(typeText);
        }

        return true;

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
                for (GoLiteralIdentifier goLiteralIdentifier : functionParameter.getIdentifiers()) {
                    GoExpr goExpr = goExprs[index];
                    if (!checkParametersExp(functionParameter.getType(), goExpr)) {
                        String name = goExpr.getText();
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
            checkFunctionTypeArguments(call, result);
        }
    }
}
