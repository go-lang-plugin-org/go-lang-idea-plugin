package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;

public class InspectionResult {
    private final InspectionManager manager;
    private final List<ProblemDescriptor> problems = new ArrayList<>();

    public InspectionResult(Project project) {
        this(InspectionManager.getInstance(project));
    }

    public InspectionResult(InspectionManager manager) {
        this.manager = manager;
    }

    public List<ProblemDescriptor> getProblems() {
        return problems;
    }

    public void addProblem(PsiElement element, String msg, LocalQuickFix... fixes) {
        addProblem(element, element, msg, fixes);
    }

    public void addProblem(PsiElement element, String msg, ProblemHighlightType type, LocalQuickFix... fixes) {
        addProblem(element, element, msg, type, fixes);
    }

    public void addProblem(PsiElement start, PsiElement end, String msg, LocalQuickFix... fixes) {
        addProblem(start, end, msg, ProblemHighlightType.GENERIC_ERROR, fixes);
    }

    public void addProblem(PsiElement start, PsiElement end, String msg, ProblemHighlightType type, LocalQuickFix... fixes) {
        TextRange startTextRange = start.getTextRange();
        TextRange endTextRange = end.getTextRange();
        if (startTextRange.getStartOffset() < endTextRange.getEndOffset()) {
            problems.add(manager.createProblemDescriptor(start, end, msg, type, true, fixes));
        }
    }

    public void addProblem(PsiElement element, int start, int end, String msg, ProblemHighlightType type, LocalQuickFix... fixes) {
        if (start < end) {
            problems.add(manager.createProblemDescriptor(element,
                                                         new TextRange(start, end),
                                                         msg, type, true, fixes));
        }
    }
}
