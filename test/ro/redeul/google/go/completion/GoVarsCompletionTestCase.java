package ro.redeul.google.go.completion;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;

public class GoVarsCompletionTestCase extends GoEditorAwareTestCase {

//    protected String getTestDataPath() {
//        return GoTestUtils.getTestDataPath() + getTestDataRelativePath();
//    }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected String getTestDataRelativePath() {
        return "completion/";
    }


    public void testDoCucTest() throws Throwable {
//        configureByFile("bibi.go");
//        testByCount(2, "", "");
    }
}
