package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.inspection.fix.AddReturnStmtFix;
import ro.redeul.google.go.inspection.fix.RemoveFunctionResultFix;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoFunctionLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
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

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class FunctionDeclarationInspection {
    private final InspectionManager manager;
    private final GoFunctionDeclaration function;
    private final List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();

    public FunctionDeclarationInspection(InspectionManager manager, GoFunctionDeclaration function) {
        this.manager = manager;
        this.function = function;
    }

    public ProblemDescriptor[] checkFunction() {
        hasResultButNoReturnAtTheEnd();
        hasDuplicateArgument();
        hasRedeclaredParameterInResultList();
        hasReturnParameterQuantityUnmatch();
        hasVariadicProblems();
        return getProblems();
    }

    public void hasResultButNoReturnAtTheEnd() {
        if (hasResult() && hasBody() && !hasReturnAtTheEnd()) {
            LocalQuickFix fix1 = new AddReturnStmtFix(function);
            LocalQuickFix fix2 = new RemoveFunctionResultFix(function);
            addProblem(function.getBlock().getLastChild(), "Function ends without a return statement", fix1, fix2);
        }
    }

    public void hasDuplicateArgument() {
        Set<String> parameters = new HashSet<String>();
        for (GoFunctionParameter fp : function.getParameters()) {
            for (GoIdentifier id : fp.getIdentifiers()) {
                if (id.isBlank()) {
                    continue;
                }

                String text = id.getText();
                if (parameters.contains(text)) {
                    addProblem(id, "Duplicate argument " + text);
                } else {
                    parameters.add(text);
                }
            }
        }
    }

    public void hasRedeclaredParameterInResultList() {
        Set<String> parameters = new HashSet<String>(getParameterNames(function.getParameters()));

        for (GoFunctionParameter fp : function.getResults()) {
            for (GoIdentifier id : fp.getIdentifiers()) {
                String text = id.getText();
                if (!id.isBlank() && parameters.contains(text)) {
                    addProblem(id, text + " redeclared in this block");
                }
            }
        }
    }

    public void hasReturnParameterQuantityUnmatch() {
        new ReturnVisitor().visitFunctionDeclaration(function);
    }

    public void hasVariadicProblems() {
        // cannot use variadic in output argument list
        for (GoFunctionParameter parameter : function.getResults()) {
            if (parameter.isVariadic()) {
                addProblem(parameter, "Cannot use ... in output argument list");
            }
        }

        GoFunctionParameter[] parameters = function.getParameters();
        if (parameters.length == 0) {
            return;
        }

        // only last argument could be variadic
        for (int i = 0; i < parameters.length - 1; i++) {
            GoFunctionParameter parameter = parameters[i];
            if (parameter.isVariadic()) {
                addProblem(parameter, "Can only use ... as final argument in list");
            }
        }
    }

    public ProblemDescriptor[] getProblems() {
        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }

    private void addProblem(String msg, LocalQuickFix... fixes) {
        PsiElement start = function.getFirstChild();
        PsiElement end = hasBody() ? function.getBlock().getPrevSibling() : function.getLastChild();
        addProblem(start, end, msg, fixes);
    }

    private void addProblem(PsiElement element, String msg, LocalQuickFix... fixes) {
        addProblem(element, element, msg, fixes);
    }

    private void addProblem(PsiElement start, PsiElement end, String msg, LocalQuickFix... fixes) {
        problems.add(manager.createProblemDescriptor(start, end, msg, ProblemHighlightType.ERROR, true, fixes));
    }

    private boolean hasResult() {
        return function.getResults().length > 0;
    }

    private boolean hasBody() {
        return function.getBlock() != null;
    }

    private boolean hasReturnAtTheEnd() {
        GoBlockStatement block = function.getBlock();
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
            for (GoIdentifier id : fp.getIdentifiers()) {
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
    private class ReturnVisitor extends GoRecursiveElementVisitor {
        private List<FunctionResult> functionResults = new ArrayList<FunctionResult>();

        @Override
        public void visitFunctionDeclaration(GoFunctionDeclaration functionDeclaration) {
            functionResults.add(new FunctionResult(functionDeclaration.getResults()));
            super.visitFunctionDeclaration(functionDeclaration);
            functionResults.remove(functionResults.size() - 1);
        }

        @Override
        public void visitFunctionLiteral(GoFunctionLiteral functionLiteral) {
            functionResults.add(new FunctionResult(functionLiteral.getResults()));
            super.visitFunctionLiteral(functionLiteral);
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
            if (element instanceof GoReturnStatement) {
                GoReturnStatement returnStatement = (GoReturnStatement) element;
                int returnCount = returnStatement.getExpressions().length;
                FunctionResult fr = functionResults.get(functionResults.size() - 1);
                if (fr == null || fr.resultCount == returnCount || returnCount == 0 && fr.namedResult) {
                    return;
                }

                if (fr.resultCount < returnCount) {
                    addProblem(element, "Too many arguments to return");
                } else {
                    addProblem(element, "Not enough arguments to return");
                }
            }
        }
    }
}
