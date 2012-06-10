package ro.redeul.google.go.inspection;

import java.util.List;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

public abstract class AbstractWholeGoFileInspection extends LocalInspectionTool {

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file,
                                         @NotNull InspectionManager manager,
                                         boolean isOnTheFly) {
        if (!(file instanceof GoFile)) {
            return null;
        }

        List<ProblemDescriptor> problems =
            doCheckFile((GoFile) file, manager, isOnTheFly);

        if ( problems == null )
            return null;

        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }

    protected abstract List<ProblemDescriptor> doCheckFile(
        @NotNull GoFile file, @NotNull InspectionManager manager, boolean isOnTheFly);
}
