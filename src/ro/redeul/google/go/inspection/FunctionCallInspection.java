package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoTypeMap;
import ro.redeul.google.go.lang.psi.types.GoTypeSlice;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import static ro.redeul.google.go.inspection.InspectionUtil.UNKNOWN_COUNT;
import static ro.redeul.google.go.inspection.InspectionUtil.checkExpressionShouldReturnOneResult;
import static ro.redeul.google.go.inspection.InspectionUtil.getExpressionResultCount;
import static ro.redeul.google.go.inspection.InspectionUtil.getFunctionIdentifier;
import static ro.redeul.google.go.inspection.InspectionUtil.getFunctionParameterCount;

public class FunctionCallInspection extends AbstractWholeGoFileInspection {
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result, boolean isOnTheFly) {
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
                if ("make".equals(baseExpression.getText())) {
                    checkMakeCall(expression, result);
                } else {
                    checkFunctionCallArguments(expression, result);
                }
            }
        }.visitFile(file);
    }

    private static void checkMakeCall(GoBuiltinCallExpression expression, InspectionResult result) {
        GoExpr[] arguments = expression.getArguments();
        GoType type = expression.getTypeArgument();
        if (type == null) {
            result.addProblem(expression, GoBundle.message("error.incorrect.make.type"));
            return;
        }

        if (type instanceof GoTypeSlice) {
            checkMakeSliceCall(expression, arguments, result);
        } else if (type instanceof GoTypeChannel) {
            checkMakeChannelCall(arguments, result);
        } else if (type instanceof GoTypeMap) {
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

    public static void checkFunctionCallArguments(GoCallOrConvExpression call, InspectionResult result) {
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
        GoLiteralIdentifier id = getFunctionIdentifier(call);
        if (id != null) {
            name = id.getName();
        }

        if (argumentCount < expectedCount) {
            result.addProblem(call, GoBundle.message("error.not.enough.arguments.in.call", name));
        } else if (argumentCount > expectedCount) {
            result.addProblem(call, GoBundle.message("error.too.many.arguments.in.call", name));
        }
    }
}
