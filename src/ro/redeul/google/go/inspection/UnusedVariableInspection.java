package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.findUsages.GoVariableUsageStatVisitor;
import ro.redeul.google.go.lang.psi.GoFile;

public class UnusedVariableInspection extends AbstractGoInspection {
    @Override
    protected ProblemDescriptor[] doCheckFile(@NotNull GoFile file, @NotNull InspectionManager manager) {
        GoVariableUsageStatVisitor visitor = new GoVariableUsageStatVisitor(manager);
        visitor.visitElement(file);
        return visitor.getProblems();
    }
}
