package ro.redeul.google.go.intentions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;

public abstract class GoIntentionTestCase extends GoEditorAwareTestCase {

    protected Intention createIntention() {
        try {
            return (Intention) Class.forName(getClass().getName().replaceAll("Test$", "")).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getTestDataRelativePath() {
        String name = getClass().getName().replaceAll("Test$", "");
        name = name.substring(name.indexOf("intentions.")).replace('.', '/');
        return lowerCaseCharAtPos(name, name.lastIndexOf("/") + 1) + '/';
    }

    private static String lowerCaseCharAtPos(String s, int charPos) {
        return s.substring(0, charPos) + s.substring(charPos, charPos + 1).toLowerCase() + s.substring(charPos + 1);
    }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
        createIntention().invoke(project, editor, file);
    }
}