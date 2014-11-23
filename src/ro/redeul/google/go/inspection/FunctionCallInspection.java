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
import ro.redeul.google.go.lang.psi.typing.*;
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

                // if this is a cast expression we should ignore it for now
                if ( expression.getBaseExpression() == null )
                    return;

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
            case Print:
            case Println:
                return validatePrintCalls(expression, args, file, result);
            case Copy:
                return validateCopyCall(expression, args, file, result);
            case Delete:
                return validateDeleteCall(expression, args, file, result);
            case Close:
                return validateCloseCall(expression, args, file, result);
            case Len:
                return validateLenCall(expression, args, file, result);
            default:
                return true;
        }
    }

    private boolean validateLenCall(GoCallOrConvExpression expr, GoExpr[] args, GoFile file, InspectionResult result) {
        if (args.length == 0) {
            result.addProblem(expr, GoBundle.message("error.call.missing.arg", "len"));
            return false;
        }


        GoType argType = GoTypes.get(args[0].getType());
        GoType underlyingType = argType.underlyingType();

        boolean validType = underlyingType.accept(new TypeVisitor<Boolean>(false) {
            @Override
            public Boolean visitArray(GoTypeArray type) { return true; }

            @Override
            public Boolean visitChannel(GoTypeChannel type) { return true; }

            @Override
            public Boolean visitMap(GoTypeMap type) { return true; }

            @Override
            public Boolean visitSlice(GoTypeSlice type) { return true; }

            @Override
            public Boolean visitPrimitive(GoTypePrimitive type) { return type.getType() == GoTypes.Builtin.String; }

            @Override
            public Boolean visitConstant(GoTypeConstant constant) {
                if (constant == null) return false;

                if (constant.getType() != GoType.Unknown)
                    return constant.getType().accept(this);
                else
                    return constant.getKind() == GoTypeConstant.Kind.String;
            }
        });

        if (!validType)
            result.addProblem(
                    args[0],
                    GoBundle.message(
                            "error.call.builtin.len.invalid.arg",
                            args[0].getText(),
                            GoTypes.getRepresentation(argType, file)));

        for (int i = 1; i < args.length; i++)
            result.addProblem(args[i], GoBundle.message("error.call.extra.arg", "len"));

        return false;
    }

    private boolean validateCloseCall(GoCallOrConvExpression expr, GoExpr[] args, GoFile file, InspectionResult result) {
        if (args.length == 0) {
            result.addProblem(expr, GoBundle.message("error.call.missing.arg", "close"));
            return false;
        }

        for (int i = 1; i < args.length; i++)
            result.addProblem(args[i], GoBundle.message("error.call.extra.arg", "close"));

        GoType argType = GoTypes.get(args[0].getType());
        GoType underlyingType = argType.underlyingType();

        if (!(underlyingType instanceof GoTypeChannel)) {
            result.addProblem(
                    args[0],
                    GoBundle.message(
                            "error.call.builtin.close.no.channel.type",
                            args[0].getText(),
                            GoTypes.getRepresentation(argType, file)));
            return false;
        }

        GoTypeChannel channel = (GoTypeChannel) underlyingType;
        if (channel.getChannelType() == GoTypeChannel.ChannelType.Receiving) {
            result.addProblem(
                    args[0],
                    GoBundle.message(
                            "error.call.builtin.close.wrong.channel.type",
                            args[0].getText()));
        }

        return false;
    }

    private boolean validateDeleteCall(GoCallOrConvExpression expr, GoExpr[] args, GoFile file, InspectionResult result) {
        if (args.length == 0) {
            result.addProblem(expr, GoBundle.message("error.call.builtin.missing.args", "delete"));
            return false;
        }

        if (args.length == 1) {
            result.addProblem(expr, GoBundle.message("error.call.builtin.delete.missing.key.arg"));
            return false;
        }

        GoType mapType = GoTypes.get(args[0].getType());
        if (!(mapType.underlyingType() instanceof GoTypeMap)) {
            result.addProblem(
                    args[0],
                    GoBundle.message("error.call.builtin.delete.wrong.map.arg", GoTypes.getRepresentation(mapType, file)));
            return false;
        }

        GoType mapKeyType = ((GoTypeMap) mapType.underlyingType()).getKeyType();
        GoType argKeyType = GoTypes.get(args[1].getType());
        if (!mapKeyType.isAssignableFrom(argKeyType)) {
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
        if (args.length < 2) {
            result.addProblem(
                    expression,
                    GoBundle.message("error.call.builtin.missing.args", "copy"));
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

        if (!arg2Type.underlyingType().isIdentical(arg1Type.underlyingType()))
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

    private boolean validateAppendCall(GoCallOrConvExpression call, GoExpr[] args, GoFile file, InspectionResult result) {
        if (args.length == 0) {
            result.addProblem(call, GoBundle.message("error.call.missing.args", "append"));
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
            result.addProblem(call, GoBundle.message("error.call.missing.args", "append"));
            return false;
        }

        GoType elementType = ((GoTypeSlice) sliceType[0].underlyingType()).getElementType();

        for (int i = 1; i < args.length; i++) {
            GoExpr arg = args[i];
            GoType[] argTypes = arg.getType();
            GoType argType = GoTypes.get(argTypes);
            GoType expectedElementType = elementType;
            if (i == args.length - 1 && call.isVariadic())
                expectedElementType = GoTypes.makeSlice(call.getProject(), elementType);

            if (argTypes.length != 1 || !expectedElementType.isAssignableFrom(argType)) {
                result.addProblem(arg,
                        GoBundle.message(
                                "warn.function.call.arg.type.mismatch",
                                arg.getText(),
                                GoTypes.getRepresentation(argType, file),
                                GoTypes.getRepresentation(elementType, file), "append"),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        new CastTypeFix(arg, elementType));
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
                    result.addProblem(args[i], GoBundle.message("error.call.extra.args", "make"));

                return getData();
            }

            @Override
            public Boolean visitChannel(GoTypeChannel type) {
                for (int i = 1; i < args.length; i++)
                    result.addProblem(args[i], GoBundle.message("error.call.extra.args", "make"));

                return getData();
            }

            @Override
            public Boolean visitSlice(GoTypeSlice type) {
                for (int i = 2; i < args.length; i++)
                    result.addProblem(args[i], GoBundle.message("error.call.extra.args", "make"));

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

        boolean isVariadicFunction = callType.isVariadic();
        GoType[] parameterTypes = callType.getParameterTypes();

        validateCall(call, getFunctionName(callType), callType.isVariadic(), callType.getParameterTypes(), call.isVariadic(), call.getArguments(), result, file);
    }

    private static void validateCall(GoCallOrConvExpression call, String name, boolean isVarFunc, GoType[] paramTypes, boolean isVarCall, GoExpr[] args, InspectionResult problems, GoFile file) {

        problems.resetCount();
        if (args.length != 1) {
            for (GoExpr argument : args) {
                if (argument.getType().length != 1) {
                    problems.addProblem(argument, GoBundle.message("error.multiple.value.in.single.value.context", argument.getText()));
                }
            }
        }
        if (problems.getCount() != 0)
            return;

        if (args.length == 1) {
            GoExpr arg = args[0];
            GoType argTypes[] = arg.getType();
            // we have a single value as a single argument, let's see if it matches
            if (argTypes.length > 1) {
                // if we have to few arguments and the definition is not variadic we complain and bail
                if (paramTypes.length < argTypes.length) {
                    if (!isVarFunc) {
                        problems.addProblem(arg, GoBundle.message("error.call.extra.args", name));
                        return;
                    }

                    if (isVarCall) {
                        problems.addProblem(arg, GoBundle.message("error.multiple.value.in.single.value.context", arg.getText()));
                        return;
                    }
                }

                if (paramTypes.length > argTypes.length) {
                    problems.addProblem(arg, GoBundle.message("error.call.missing.args", name));
                    return;
                }

                // validate argument types
                int max = argTypes.length;
                if (paramTypes.length < max && !isVarFunc)
                    max = paramTypes.length;

                for (int i = 0; i < max; i++) {
                    GoType argType = argTypes[i];
                    GoType paramType = i < paramTypes.length ? paramTypes[i] : paramTypes[paramTypes.length - 1];


                    if (!paramType.isAssignableFrom(argType)) {
                        problems.addProblem(arg,
                                GoBundle.message(
                                        "error.call.arg.type.mismatch.multival",
                                        GoTypes.getRepresentation(argType, file),
                                        GoTypes.getRepresentation(paramType, file),
                                        name),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        break;
                    }
                }

                return;
            }
        }

        int exprIdx = 0, exprCount = args.length;
        for (; exprIdx < exprCount; exprIdx++) {
            GoExpr expr = args[exprIdx];

            GoType expectedType = GoType.Unknown;

            if (exprIdx < paramTypes.length) {
                expectedType = paramTypes[exprIdx];
            }

            if (exprIdx >= paramTypes.length) {
                if (!isVarFunc) {
                    problems.addProblem(expr, GoBundle.message("error.call.extra.arg", name));
                    continue;
                }

                expectedType = paramTypes[paramTypes.length - 1];
            }

            if (exprIdx == paramTypes.length - 1) {
                if (isVarCall)
                    expectedType = GoTypes.makeSlice(expr.getProject(), expectedType);
            }

            GoType argumentType = GoTypes.get(expr.getType());

            if (!expectedType.isAssignableFrom(argumentType)) {
                problems.addProblem(expr,
                        GoBundle.message(
                                "warn.function.call.arg.type.mismatch",
                                expr.getText(),
                                GoTypes.getRepresentation(argumentType, file),
                                GoTypes.getRepresentation(expectedType, file),
                                name),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        new CastTypeFix(expr, expectedType));
            }
        }

        if (exprIdx < paramTypes.length && !(exprIdx == paramTypes.length - 1 && isVarFunc))
            problems.addProblem(call, GoBundle.message("error.call.missing.args", name));
    }

    private static String getFunctionName(GoTypeFunction callType) {
        return String.format("%s", callType.getPsiType().getName());
    }
}
