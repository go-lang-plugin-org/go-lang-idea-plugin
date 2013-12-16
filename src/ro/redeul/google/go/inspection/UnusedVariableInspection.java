package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.findUsages.GoVariableUsageStatVisitor2;
import ro.redeul.google.go.lang.psi.GoFile;

public class UnusedVariableInspection extends AbstractWholeGoFileInspection {

    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull InspectionResult result) {
        new GoVariableUsageStatVisitor2(result).visitFile(file);
    }
}
