package ro.redeul.google.go.refactoring.introduce;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.FileDataBasedTest;
import ro.redeul.google.go.lang.psi.GoFile;

public abstract class AbstractIntroduceTest extends FileDataBasedTest {
    protected abstract GoIntroduceHandlerBase createHandler();
    protected abstract String getItemName();

    @Override
    protected void invoke(Project project, Editor myEditor, GoFile file) {
        createHandler().invoke(getProject(), myEditor, file, null);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "refactoring/introduce/" + getItemName() + "/";
    }
}
