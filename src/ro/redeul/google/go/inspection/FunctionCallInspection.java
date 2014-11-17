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
    protected void doCheckFile(@NotNull final GoFile file, @NotNull final InspectionResult result) {
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

                    if (validateBuiltinCall(builtin, callType, expression, file, result))
                        validateCallArguments(callType, expression, file, result);
                }
            }

        }.visitFile(file);
    }

    private boolean validateBuiltinCall(GoFunctions.Builtin builtin, GoTypeFunction callType,
                                        GoCallOrConvExpression expression,
                                        GoFile file, InspectionResult result) {
        GoPsiType typeArg = expression.getTypeArgument();
        GoExpr[] args = expression.getArguments();

        switch (builtin) {
            case New:
                return validateNewCall(expression, result, typeArg, args);
            case Make:
                return validateMakeCall(callType, expression, file, result, typeArg, args);
            case Append:
                return validateAppendCall(expression, args, file, result);
            case Print: case Println:
                return validatePrintCalls(expression, args, file, result);
            case Copy:
                return validateCopyCall(expression, args, file, result);
            case Delete:
                return validateDeleteCall(expression, args, file, result);
            default:
                return true;
        }
    }

    private boolean validateDeleteCall(GoCallOrConvExpression expr, GoExpr[] args, GoFile file, InspectionResult result) {
        if ( args.length == 0 ) {
            result.addProblem(expr, GoBundle.message("error.call.missing.args", "delete"));
            return false;
        }

        if ( args.length == 1 ) {
            result.addProblem(expr, GoBundle.message("error.call.builtin.delete.missing.key.arg"));
            return false;
        }

        GoType mapType = GoTypes.get(args[0].getType());
        if ( !(mapType.underlyingType() instanceof GoTypeMap) ) {
            result.addProblem(
                    args[0],
                    GoBundle.message("error.call.builtin.delete.wrong.map.arg", GoTypes.getRepresentation(mapType, file)));
            return false;
        }

        GoType mapKeyType = ((GoTypeMap) mapType.underlyingType()).getKeyType();
        GoType argKeyType = GoTypes.get(args[1].getType());
        if ( !mapKeyType.isAssignableFrom(argKeyType) ) {
            result.addProblem(
                    args[1],
                    GoBundle.message(
                            "error.call.builtin.delete.wrong.key.arg",
                            args[1].getText(),
                            GoTypes.getRepresentation(argKeyType, file),
                            GoTypes.getRepresentation(mapKeyType, file)),
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    new CastTypeFix(args[1], mapKeyType));
            return false;
        }

        for (int i = 2; i < args.length; i++)
            result.addProblem(args[i], GoBundle.message("error.call.extra.arg", "delete"));

        return false;
    }

    private boolean validateMakeCall(GoTypeFunction callType, GoCallOrConvExpression expression, GoFile file, InspectionResult result, GoPsiType typeArg, GoExpr[] args) {
        if (typeArg != null)
            return validateMakeCall(callType, expression, file, result);

        if (args.length == 0)
            result.addProblem(expression, GoBundle.message("error.call.builtin.missing.type.arg", "make"));
        else
            result.addProblem(args[0], GoBundle.message("error.expression.is.not.a.type", args[0].getText()));

        return false;
    }

    private boolean validateNewCall(GoCallOrConvExpression expression, InspectionResult result, GoPsiType typeArg, GoExpr[] args) {
        if (typeArg != null) return true;

        if (args.length == 0)
            result.addProblem(expression, GoBundle.message("error.call.builtin.missing.type.arg", "new"));
        else
            result.addProblem(args[0], GoBundle.message("error.expression.is.not.a.type", args[0].getText()));

        return false;
    }

    private boolean validateCopyCall(GoCallOrConvExpression expression, GoExpr[] args, GoFile file, InspectionResult result) {
        if ( args.length < 2 ) {
            result.addProblem(
                    expression,
                    GoBundle.message("error.call.missing.args", "copy"));
            return false;
        }

        GoType arg1Type = GoTypes.get(args[0].getType());
        GoType arg2Type = GoTypes.get(args[1].getType());

        if (!(arg1Type.underlyingType() instanceof GoTypeSlice)) {
            result.addProblem(
                    args[0],
                    GoBundle.message(
                            "error.call.builtin.copy.wrong.1st.arg",
                            GoTypes.getRepresentation(arg1Type, file)));
            return false;
        }

        if (!(arg2Type.underlyingType() instanceof GoTypeSlice)) {
            result.addProblem(
                    args[1],
                    GoBundle.message(
                            "error.call.builtin.copy.wrong.2nd.arg",
                            GoTypes.getRepresentation(arg1Type, file)));
            return false;
        }

        if ( !arg2Type.underlyingType().isIdentical(arg1Type.underlyingType()))
            result.addProblem(
                    args[1],
                    GoBundle.message(
                            "error.call.builtin.copy.args.type.mismatch",
                            GoTypes.getRepresentation(arg1Type.underlyingType(), file),
                            GoTypes.getRepresentation(arg2Type.underlyingType(), file)),
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    new CastTypeFix(args[1], arg1Type)
            );

        for (int i = 2; i < args.length; i++)
            result.addProblem(args[i], GoBundle.message("error.call.extra.arg", "copy"));

        return false;
    }

    private boolean validatePrintCalls(GoCallOrConvExpression expression, GoExpr[] args, GoFile file, InspectionResult result) {
        for (GoExpr arg : args) {
            GoType[] exprType = arg.getType();
            if (exprType.length != 1)
                result.addProblem(arg, GoBundle.message("error.multiple.value.in.single.value.context", arg.getText()));
        }

        return false;
    }

    private boolean validateAppendCall(GoCallOrConvExpression expression, GoExpr[] args, GoFile file, InspectionResult result) {
        if (args.length == 0) {
            result.addProblem(expression, GoBundle.message("error.not.enough.arguments.in.call", "append"));
            return false;
        }

        GoType sliceType[] = args[0].getType();
        if (sliceType.length == 0 || !(sliceType[0].underlyingType() instanceof GoTypeSlice)) {
            result.addProblem(args[0],
                    GoBundle.message(
                            "error.calls.append.first.argument.must.be.slice",
                            GoTypes.getRepresentation(sliceType[0], file)));
            return false;
        }

        if (args.length == 1) {
            result.addProblem(
                    expression,
                    GoBundle.message(
                            "error.not.enough.arguments.in.call", "append"));
            return false;
        }

        GoType elementType = ((GoTypeSlice) sliceType[0].underlyingType()).getElementType();

        for (int i = 1; i < args.length; i++) {
            GoType argType[] = args[i].getType();
            if (argType.length != 1 || !elementType.isAssignableFrom(argType[0])) {
                result.addProblem(args[i],
                        GoBundle.message(
                                "warn.function.call.arg.type.mismatch",
                                args[i].getText(),
                                GoTypes.getRepresentation(argType[0], file),
                                GoTypes.getRepresentation(elementType, file), "append"),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        new CastTypeFix(args[i], elementType));
            }
        }

        return false;
    }

    private boolean validateMakeCall(GoTypeFunction type, final GoCallOrConvExpression call, GoFile file, final InspectionResult result) {
        GoPsiType psiTypeArg = call.getTypeArgument();

        GoType typeArgument = GoTypes.fromPsi(psiTypeArg);
        GoType underlyingType = typeArgument.underlyingType();

        if (!(underlyingType instanceof GoTypeSlice) && !(underlyingType instanceof GoTypeMap) && !(underlyingType instanceof GoTypeChannel)) {
            result.addProblem(
                    psiTypeArg,
                    GoBundle.message("error.call.builtin.make.cannot.make.type", GoTypes.getRepresentation(typeArgument, file)));
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

                if (args.length == 0)
                    result.addProblem(call, GoBundle.message("error.call.missing.arg", call.getText()));

                return getData();
            }
        });

        return false;
    }

    private static void validateCallArguments(GoTypeFunction callType, GoCallOrConvExpression call, GoFile file, InspectionResult result) {

        String functionName = getFunctionName(callType);
        GoExpr[] arguments = call.getArguments();
        if (arguments == null) return;

        GoType[] parameterTypes = callType.getParameterTypes();
        boolean isVariadicCall = callType.isVariadic();

        int i = 0, exprCount = arguments.length;
        for (; i < exprCount; i++) {
            GoExpr expr = arguments[i];
            GoType parameterType;

            if (i < parameterTypes.length) {
                parameterType = parameterTypes[i];
            } else {
                if (isVariadicCall) {
                    parameterType = parameterTypes[parameterTypes.length - 1];
                } else {
                    result.addProblem(call, GoBundle.message("error.too.many.arguments.in.call", functionName));
                    continue;
                }
            }

            GoType[] exprType = expr.getType();
            if (exprType.length != 1 && parameterTypes.length > 1) {
                result.addProblem(expr, GoBundle.message("error.multiple.value.in.single.value.context", expr.getText()));
                continue;
            }

            if (!parameterType.isAssignableFrom(exprType[0])) {
                result.addProblem(expr,
                        GoBundle.message(
                                "warn.function.call.arg.type.mismatch",
                                expr.getText(),
                                GoTypes.getRepresentation(exprType[0], file),
                                GoTypes.getRepresentation(parameterType, file),
                                functionName),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        new CastTypeFix(expr, parameterType));
            }
        }

        if (i < parameterTypes.length && !(i == parameterTypes.length - 1 && isVariadicCall))
            result.addProblem(call, GoBundle.message("error.not.enough.arguments.in.call", functionName));
    }

    private static String getFunctionName(GoTypeFunction callType) {
        return String.format("%s", callType.getPsiType().getName());
    }
}
