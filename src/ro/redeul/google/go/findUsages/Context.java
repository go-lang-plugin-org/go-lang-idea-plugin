package ro.redeul.google.go.findUsages;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.inspection.fix.RemoveVariableFix;
import ro.redeul.google.go.lang.psi.GoPsiElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Context {
    public final List<ProblemDescriptor> problems;
    public final InspectionManager manager;
    public final List<Map<String, VariableUsage>> variables = new ArrayList<Map<String, VariableUsage>>();

    Context(List<ProblemDescriptor> problems, InspectionManager manager, Map<String, VariableUsage> global) {
        this.problems = problems;
        this.manager = manager;
        this.variables.add(global);
    }

    public Map<String, VariableUsage> addNewScopeLevel() {
        Map<String, VariableUsage> variables = new HashMap<String, VariableUsage>();
        this.variables.add(variables);
        return variables;
    }

    public Map<String, VariableUsage> popLastScopeLevel() {
        return variables.remove(variables.size() - 1);
    }

    public void unusedVariable(VariableUsage variableUsage) {
        if (variableUsage.isBlank()) {
            return;
        }

        addProblem(variableUsage, "Unused variable", ProblemHighlightType.ERROR, new RemoveVariableFix());
    }

    public void unusedParameter(VariableUsage variableUsage) {
        if (!variableUsage.isBlank()) {
            addProblem(variableUsage, "Unused parameter", ProblemHighlightType.LIKE_UNUSED_SYMBOL, null);
        }
    }

    public void unusedGlobalVariable(VariableUsage variableUsage) {
        addProblem(variableUsage, "Unused global", ProblemHighlightType.LIKE_UNUSED_SYMBOL, new RemoveVariableFix());
    }

    public void undefinedVariable(VariableUsage variableUsage) {
        addProblem(variableUsage, "Undefined variable", ProblemHighlightType.ERROR, null);
    }

    private void addProblem(VariableUsage variableUsage, String desc, ProblemHighlightType highlightType, @Nullable LocalQuickFix fix) {
        problems.add(manager.createProblemDescriptor(variableUsage.element, desc, fix, highlightType, true));
    }

    public void addDefinition(GoPsiElement element) {
        variables.get(variables.size() - 1).put(element.getText(), new VariableUsage(element));
    }

    public void addUsage(GoPsiElement element) {
        for (int i = variables.size() - 1; i >= 0; i--) {
            VariableUsage variableUsage = variables.get(i).get(element.getText());
            if (variableUsage != null) {
                variableUsage.addUsage(element);
                return;
            }
        }

        undefinedVariable(new VariableUsage(element));
    }
}
