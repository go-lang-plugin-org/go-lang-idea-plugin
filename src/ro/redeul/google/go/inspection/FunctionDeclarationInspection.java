package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.AddReturnStmtFix;
import ro.redeul.google.go.inspection.fix.RemoveFunctionResultFix;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ro.redeul.google.go.inspection.InspectionUtil.UNKNOWN_COUNT;
import static ro.redeul.google.go.inspection.InspectionUtil.checkExpressionShouldReturnOneResult;
import static ro.redeul.google.go.inspection.InspectionUtil.getFunctionCallResultCount;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class FunctionDeclarationInspection
    extends AbstractWholeGoFileInspection
{
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result, boolean isOnTheFly) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitFunctionDeclaration(GoFunctionDeclaration functionDeclaration) {
                checkFunction(result, functionDeclaration);
            }

            @Override
            public void visitMethodDeclaration(GoMethodDeclaration methodDeclaration) {
                checkFunction(result, methodDeclaration);
            }
        }.visitFile(file);
    }

    public static void checkFunction(InspectionResult result, GoFunctionDeclaration function) {
        Context ctx = new Context(result, function);
        hasResultButNoReturnAtTheEnd(ctx);
        hasDuplicateArgument(ctx);
        hasRedeclaredParameterInResultList(ctx);
        hasReturnParameterCountMismatch(ctx);
        hasVariadicProblems(ctx);
    }

    public static void hasResultButNoReturnAtTheEnd(Context ctx) {
        if (hasResult(ctx) && hasBody(ctx) && !hasReturnAtTheEnd(ctx)) {
            LocalQuickFix fix1 = new AddReturnStmtFix(ctx.function);
            LocalQuickFix fix2 = new RemoveFunctionResultFix(ctx.function);
            PsiElement element = ctx.function.getBlock().getLastChild();
            ctx.result.addProblem(element, GoBundle.message("error.no.return.found"), fix1, fix2);
        }
    }

    public static void hasDuplicateArgument(Context ctx) {
        Set<String> parameters = new HashSet<String>();
        for (GoFunctionParameter fp : ctx.function.getParameters()) {
            for (GoLiteralIdentifier id : fp.getIdentifiers()) {
                if (id.isBlank()) {
                    continue;
                }

                String text = id.getText();
                if (parameters.contains(text)) {
                    ctx.result.addProblem(id, GoBundle.message("error.duplicate.argument", text));
                } else {
                    parameters.add(text);
                }
            }
        }
    }

    public static void hasRedeclaredParameterInResultList(Context ctx) {
        Set<String> parameters = new HashSet<String>(getParameterNames(ctx.function.getParameters()));

        for (GoFunctionParameter fp : ctx.function.getResults()) {
            for (GoLiteralIdentifier id : fp.getIdentifiers()) {
                String text = id.getText();
                if (!id.isBlank() && parameters.contains(text)) {
                    ctx.result.addProblem(id, GoBundle.message("error.redeclared.in.block", text));
                }
            }
        }
    }

    public static void hasReturnParameterCountMismatch(Context ctx) {
        new ReturnVisitor(ctx.result).visitFunctionDeclaration(ctx.function);
    }

    public static void hasVariadicProblems(Context ctx) {
        // cannot use variadic in output argument list
        for (GoFunctionParameter parameter : ctx.function.getResults()) {
            if (parameter.isVariadic()) {
                ctx.result.addProblem(parameter, GoBundle.message("error.output.variadic"));
            }
        }

        GoFunctionParameter[] parameters = ctx.function.getParameters();
        if (parameters.length == 0) {
            return;
        }

        // only last argument could be variadic
        for (int i = 0; i < parameters.length - 1; i++) {
            GoFunctionParameter parameter = parameters[i];
            if (parameter.isVariadic()) {
                ctx.result.addProblem(parameter, GoBundle.message("error.variadic.not.the.last"));
            }
        }
    }

    private static boolean hasResult(Context ctx) {
        return ctx.function.getResults().length > 0;
    }

    private static boolean hasBody(Context ctx) {
        return ctx.function.getBlock() != null;
    }

    private static boolean hasReturnAtTheEnd(Context ctx) {
        GoBlockStatement block = ctx.function.getBlock();
        if (block == null) {
            return false;
        }

        PsiElement lastChild = getPrevSiblingIfItsWhiteSpaceOrComment(block.getLastChild());
        if (lastChild == null || !"}".equals(lastChild.getText())) {
            return false;
        }

        lastChild = getPrevSiblingIfItsWhiteSpaceOrComment(lastChild.getPrevSibling());
        return isNodeOfType(lastChild, GoElementTypes.RETURN_STATEMENT);
    }

    private static List<String> getParameterNames(GoFunctionParameter[] parameters) {
        List<String> parameterNames = new ArrayList<String>();
        for (GoFunctionParameter fp : parameters) {
            for (GoLiteralIdentifier id : fp.getIdentifiers()) {
                if (!id.isBlank()) {
                    parameterNames.add(id.getText());
                }
            }
        }
        return parameterNames;
    }

    private static class FunctionResult {
        public final boolean namedResult;
        public final int resultCount;
        private FunctionResult(GoFunctionParameter[] results) {
            namedResult = results.length == 0 || results[0].getIdentifiers().length > 0;
            resultCount = getResultCount(results);
        }

        private static int getResultCount(GoFunctionParameter[] results) {
            int count = 0;
            for (GoFunctionParameter result : results) {
                count += Math.max(result.getIdentifiers().length, 1);
            }
            return count;
        }
    }

    /**
     * Recursively look for return statement, and compare its expression list with function's result list
     */
    private static class ReturnVisitor extends GoRecursiveElementVisitor {
        private List<FunctionResult> functionResults = new ArrayList<FunctionResult>();
        private final InspectionResult result;

        public ReturnVisitor(InspectionResult result) {
            this.result = result;
        }

        @Override
        public void visitFunctionDeclaration(GoFunctionDeclaration functionDeclaration) {
            functionResults.add(new FunctionResult(functionDeclaration.getResults()));
            super.visitFunctionDeclaration(functionDeclaration);
            functionResults.remove(functionResults.size() - 1);
        }

        @Override
        public void visitFunctionLiteral(GoLiteralFunction literalFunction) {
            functionResults.add(new FunctionResult(literalFunction.getResults()));
            super.visitFunctionLiteral(literalFunction);
            functionResults.remove(functionResults.size() - 1);
        }

        @Override
        public void visitMethodDeclaration(GoMethodDeclaration methodDeclaration) {
            functionResults.add(new FunctionResult(methodDeclaration.getResults()));
            super.visitMethodDeclaration(methodDeclaration);
            functionResults.remove(functionResults.size() - 1);
        }

        @Override
        public void visitElement(GoPsiElement element) {
            super.visitElement(element);

            if (!(element instanceof GoReturnStatement)) {
                return;
            }

            GoReturnStatement returnStatement = (GoReturnStatement) element;
            GoExpr[] expressions = returnStatement.getExpressions();
            int returnCount = expressions.length;
            if (returnCount == 1) {
                if (expressions[0] instanceof GoCallOrConversionExpression) {
                    int count = getFunctionCallResultCount((GoCallOrConversionExpression) expressions[0]);
                    if (count != UNKNOWN_COUNT) {
                        returnCount = count;
                    }
                }
            } else {
                checkExpressionShouldReturnOneResult(expressions, result);
            }

            FunctionResult fr = functionResults.get(functionResults.size() - 1);
            if (fr == null || fr.resultCount == returnCount || returnCount == 0 && fr.namedResult) {
                return;
            }

            if (fr.resultCount < returnCount) {
                result.addProblem(element, GoBundle.message("error.too.many.arguments.to.return"));
            } else {
                result.addProblem(element, GoBundle.message("error.not.enough.arguments.to.return"));
            }
        }
    }

    private static class Context {
        public final InspectionResult result;
        public final GoFunctionDeclaration function;

        private Context(InspectionResult result, GoFunctionDeclaration function) {
            this.result = result;
            this.function = function;
        }
    }
}
