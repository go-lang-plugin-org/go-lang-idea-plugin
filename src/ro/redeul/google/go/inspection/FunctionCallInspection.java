package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

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
        GoLiteralIdentifier id = getCallFunctionIdentifier(call);
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
