package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

import java.util.List;

public abstract class AbstractWholeGoFileInspection extends LocalInspectionTool {

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file,
                                         @NotNull InspectionManager manager,
                                         boolean isOnTheFly) {
        if (!(file instanceof GoFile))
            return null;

        InspectionResult result = new InspectionResult(manager);

        doCheckFile((GoFile) file, result);

        List<ProblemDescriptor> problems = result.getProblems();
        return problems.toArray(new ProblemDescriptor[problems.size()]);
    }

    protected abstract void doCheckFile(@NotNull GoFile file, @NotNull InspectionResult result);
}
