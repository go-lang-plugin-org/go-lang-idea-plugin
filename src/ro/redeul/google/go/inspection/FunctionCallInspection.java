package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.CastTypeFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoFunctions;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;
import ro.redeul.google.go.lang.psi.typing.GoTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoTypeMap;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.typing.TypeVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class FunctionCallInspection extends AbstractWholeGoFileInspection {

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitBuiltinCallExpression(GoBuiltinCallOrConversionExpression expression) {
                visitCallOrConvExpression(expression);
            }

            @Override
            public void visitCallOrConvExpression(GoCallOrConvExpression expression) {
                super.visitCallOrConvExpression(expression);

                GoType exprTypes[] = expression.getBaseExpression().getType();
                if (exprTypes.length == 1 && exprTypes[0] != null && exprTypes[0] instanceof GoTypeFunction) {

                    GoTypeFunction callType = (GoTypeFunction) exprTypes[0];

                    String functionName = getFunctionName(callType);
                    GoFunctions.Builtin builtin = GoFunctions.getFunction(functionName);

                    if (validateBuiltinCall(builtin, callType, expression, result))
                        validateCallArguments(callType, expression, result);
                }
            }

        }.visitFile(file);
    }

    private boolean validateBuiltinCall(GoFunctions.Builtin builtin, GoTypeFunction callType, GoCallOrConvExpression expression, InspectionResult result) {
        GoPsiType typeArg = expression.getTypeArgument();
        GoExpr[] args = expression.getArguments();

        switch (builtin) {
            case New:
                if (typeArg != null) return true;

                if (args.length == 0)
                    result.addProblem(expression, GoBundle.message("error.missing.type.argument", "new"));
                else
                    result.addProblem(args[0], GoBundle.message("error.expression.is.not.a.type", args[0].getText()));

                return false;
            case Make:
                if (typeArg != null)
                    return validateMakeCall(callType, expression, result);

                if (args.length == 0)
                    result.addProblem(expression, GoBundle.message("error.missing.type.argument", "make"));
                else
                    result.addProblem(args[0], GoBundle.message("error.expression.is.not.a.type", args[0].getText()));

                return false;
            case Append:

            default:
                return true;
        }
    }

    private boolean validateMakeCall(GoTypeFunction type, final GoCallOrConvExpression call, final InspectionResult result) {
        GoPsiType psiTypeArg = call.getTypeArgument();
        GoFile file = (GoFile) psiTypeArg.getContainingFile();

        GoType typeArgument = GoTypes.fromPsi(psiTypeArg);
        GoType underlyingType = typeArgument.underlyingType();

        if (!(underlyingType instanceof GoTypeSlice) && !(underlyingType instanceof GoTypeMap) && !(underlyingType instanceof GoTypeChannel)) {
            result.addProblem(
                    psiTypeArg,
                    GoBundle.message("error.cannot.make.type", GoTypes.getRepresentation(typeArgument, file)));
        }

        final GoExpr[] args = call.getArguments();

        underlyingType.accept(new TypeVisitor<Boolean>(true) {
            @Override
            public Boolean visitMap(GoTypeMap type) {
                for (int i = 1; i < args.length; i++)
                    result.addProblem(args[i], GoBundle.message("error.too.many.arguments.in.call", "make"));

                return getData();
            }

            @Override
            public Boolean visitChannel(GoTypeChannel type) {
                for (int i = 1; i < args.length; i++)
                    result.addProblem(args[i], GoBundle.message("error.too.many.arguments.in.call", "make"));

                return getData();
            }

            @Override
            public Boolean visitSlice(GoTypeSlice type) {
                for (int i = 2; i < args.length; i++)
                    result.addProblem(args[i], GoBundle.message("error.too.many.arguments.in.call", "make"));

                if ( args.length == 0 )
                    result.addProblem(call, GoBundle.message("error.missing.argument", call.getText()));

                return getData();
            }
        });

        return false;
    }

   private static void validateCallArguments(GoTypeFunction callType, GoCallOrConvExpression call, InspectionResult result) {

        String functionName = getFunctionName(callType);
        GoExpr[] arguments = call.getArguments();
        if (arguments == null) return;

        GoType[] parameterTypes = callType.getParameterTypes();

        GoFile file = (GoFile) call.getContainingFile();

        int i = 0, exprCount = arguments.length;
        for (; i < exprCount; i++) {
            GoExpr expr = arguments[i];

            if (i >= parameterTypes.length) {
                result.addProblem(call, GoBundle.message("error.too.many.arguments.in.call", functionName));
                continue;
            }

            GoType[] exprType = expr.getType();
            if (exprType.length != 1) {
                result.addProblem(expr, GoBundle.message("error.multiple.value.in.single.value.context", expr.getText()));
                continue;
            }

            if (!parameterTypes[i].isAssignableFrom(exprType[0])) {
                result.addProblem(expr,
                        GoBundle.message(
                                "warn.function.call.arg.type.mismatch",
                                expr.getText(),
                                GoTypes.getRepresentation(exprType[0], file),
                                GoTypes.getRepresentation(parameterTypes[i], file),
                                functionName),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        new CastTypeFix(expr, parameterTypes[i]));
            }
        }

        if (i < parameterTypes.length)
            result.addProblem(call, GoBundle.message("error.not.enough.arguments.in.call", functionName));
    }

    private static String getFunctionName(GoTypeFunction callType) {
        return String.format("%s", callType.getPsiType().getName());
    }
}
