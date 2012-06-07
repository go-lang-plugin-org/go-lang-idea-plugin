package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoFunctionLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor2;

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
        return getProblems();
    }

    public void hasResultButNoReturnAtTheEnd() {
        if (hasResult() && hasBody() && !hasReturnAtTheEnd()) {
            addProblem("Function ends without a return statement");
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
        new ReturnVisitor().visitElement(function);
    }

    public ProblemDescriptor[] getProblems() {
        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }

    private void addProblem(String msg) {
        PsiElement start = function.getFirstChild();
        PsiElement end = hasBody() ? function.getBlock().getPrevSibling() : function.getLastChild();
        addProblem(start, end, msg);
    }

    private void addProblem(PsiElement element, String msg) {
        addProblem(element, element, msg);
    }

    private void addProblem(PsiElement start, PsiElement end, String msg) {
        problems.add(manager.createProblemDescriptor(start, end, msg, ProblemHighlightType.ERROR, true));
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
    private class ReturnVisitor extends GoRecursiveElementVisitor2 {
        private List<FunctionResult> functionResults = new ArrayList<FunctionResult>();
        @Override
        protected void beforeVisitElement(PsiElement element) {
            if (element instanceof GoFunctionDeclaration) {
                functionResults.add(new FunctionResult(((GoFunctionDeclaration) element).getResults()));
            } else if (element instanceof GoFunctionLiteral) {
                functionResults.add(new FunctionResult(((GoFunctionLiteral) element).getResults()));
            } else if (element instanceof GoReturnStatement) {
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

        @Override
        protected void afterVisitElement(PsiElement element) {
            if (element instanceof GoFunctionDeclaration || element instanceof GoFunctionLiteral) {
                functionResults.remove(functionResults.size() - 1);
            }
        }
    }
}
