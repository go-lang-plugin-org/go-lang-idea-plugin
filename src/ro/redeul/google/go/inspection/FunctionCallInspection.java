package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
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
            public void visitCallOrConvExpressions(GoCallOrConvExpression expression) {
                super.visitCallOrConvExpressions(expression);

                checkFunctionCallArguments(expression, result);
            }

            @Override
            public void visitBuiltinCallExpression(GoBuiltinCallExpression expression) {
                super.visitBuiltinCallExpression(expression);

                checkFunctionCallArguments(expression, result);
            }
        }.visitFile(file);
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
