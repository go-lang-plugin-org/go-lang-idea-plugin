package ro.redeul.google.go.refactoring;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.refactoring.introduce.GoIntroduceHandlerBase;

public abstract class GoRefactoringTestCase<T extends GoIntroduceHandlerBase>
    extends GoEditorAwareTestCase {

    protected abstract T createHandler();

    @Override
    protected String getTestDataRelativePath() {
        return "refactoring/" + getClass().getSimpleName().replaceAll("[tT]est", "") + "/";
    }

    @Override
    protected void invoke(Project project, Editor myEditor, GoFile file) {
        createHandler().invoke(getProject(), myEditor, file, null);
    }
}
