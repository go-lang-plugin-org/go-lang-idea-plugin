package ro.redeul.google.go.inspection.fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static ro.redeul.google.go.inspection.fix.FixUtil.removeWholeElement;

public class DeleteStmtFix implements LocalQuickFix {

    @NotNull
    @Override
    public String getName() {
        return "Delete whole statement";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Variable Declaration";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        removeWholeElement(descriptor.getStartElement());
    }
}