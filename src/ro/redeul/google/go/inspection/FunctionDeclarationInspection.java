package ro.redeul.google.go.inspection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.getPrevSiblingIfItsWhiteSpace;
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
        // TODO: implement this after method of getting expression list of return statement is provided
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

        PsiElement lastChild = getPrevSiblingIfItsWhiteSpace(block.getLastChild());
        if (lastChild == null || !"}".equals(lastChild.getText())) {
            return false;
        }

        lastChild = getPrevSiblingIfItsWhiteSpace(lastChild.getPrevSibling());
        return isNodeOfType(lastChild, GoElementTypes.RETURN_STATEMENT);
    }

    private List<String> getParameterNames(GoFunctionParameter[] parameters) {
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

    private static GoFunctionParameter[] getFunctionParameters(GoFunctionParameterList list) {
        if (list == null) {
            return new GoFunctionParameter[0];
        }

        GoFunctionParameter[] fps = list.getFunctionParameters();
        return fps == null ? new GoFunctionParameter[0] : fps;
    }
}
