package ro.redeul.google.go.refactoring.introduce;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import ro.redeul.google.go.FileDataBasedTest;

public abstract class AbstractIntroduceTest extends FileDataBasedTest {
    protected abstract GoIntroduceHandlerBase createHandler();
    protected abstract String getItemName();

    @Override
    protected void invoke(Project project, Editor myEditor, PsiFile file) {
        createHandler().invoke(getProject(), myEditor, file, null);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "refactoring/introduce/" + getItemName() + "/";
    }
}
