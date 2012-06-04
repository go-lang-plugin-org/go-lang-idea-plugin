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

class Ctx {
    public final List<ProblemDescriptor> problems;
    public final InspectionManager manager;
    public final List<Map<String, Var>> variables = new ArrayList<Map<String, Var>>();

    Ctx(List<ProblemDescriptor> problems, InspectionManager manager, Map<String, Var> global) {
        this.problems = problems;
        this.manager = manager;
        this.variables.add(global);
    }

    public Map<String, Var> addNewScopeLevel() {
        Map<String, Var> variables = new HashMap<String, Var>();
        this.variables.add(variables);
        return variables;
    }

    public Map<String, Var> popLastScopeLevel() {
        return variables.remove(variables.size() - 1);
    }

    public void unusedVariable(Var var) {
        if (var.isBlank()) {
            return;
        }

        addProblem(var, "Unused variable", ProblemHighlightType.ERROR, new RemoveVariableFix());
    }

    public void unusedParameter(Var var) {
        if (!var.isBlank()) {
            addProblem(var, "Unused parameter", ProblemHighlightType.LIKE_UNUSED_SYMBOL, null);
        }
    }

    public void unusedGlobalVariable(Var var) {
        addProblem(var, "Unused global", ProblemHighlightType.LIKE_UNUSED_SYMBOL, new RemoveVariableFix());
    }

    public void undefinedVariable(Var var) {
        addProblem(var, "Undefined variable", ProblemHighlightType.ERROR, null);
    }

    private void addProblem(Var var, String desc, ProblemHighlightType highlightType, @Nullable LocalQuickFix fix) {
        problems.add(manager.createProblemDescriptor(var.element, desc, fix, highlightType, true));
    }

    public void addDefinition(GoPsiElement element) {
        variables.get(variables.size() - 1).put(element.getText(), new Var(element));
    }

    public void addUsage(GoPsiElement element) {
        for (int i = variables.size() - 1; i >= 0; i--) {
            Var var = variables.get(i).get(element.getText());
            if (var != null) {
                var.addUsage(element);
                return;
            }
        }

        undefinedVariable(new Var(element));
    }
}
