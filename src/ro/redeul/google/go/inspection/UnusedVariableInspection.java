package ro.redeul.google.go.inspection;

import java.util.List;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.findUsages.GoVariableUsageStatVisitor;
import ro.redeul.google.go.lang.psi.GoFile;

public class UnusedVariableInspection extends AbstractWholeGoFileInspection {

    @Override
    protected List<ProblemDescriptor> doCheckFile(@NotNull GoFile file,
                                                  @NotNull InspectionManager manager,
                                                  boolean isOnTheFly) {
        GoVariableUsageStatVisitor visitor =
            new GoVariableUsageStatVisitor(manager);

        visitor.visitFile(file);
        return visitor.getProblems();
    }
}
