package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

public abstract class AbstractGoInspection extends LocalInspectionTool {
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!(file instanceof GoFile)) {
            return null;
        }

        return doCheckFile((GoFile) file, manager);
    }

    protected abstract ProblemDescriptor[] doCheckFile(@NotNull GoFile file, @NotNull InspectionManager manager);
}
