package ro.redeul.google.go.lang.completion;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.junit.Test;
import ro.redeul.google.go.GoEditorAwareTestCase;
import ro.redeul.google.go.lang.psi.GoFile;

import java.io.File;

/**
 * User: mtoader
 * Date: Aug 20, 2010
 * Time: 5:29:50 PM
 */
public abstract class KeywordCompletionTestCase extends GoEditorAwareTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return "completion" + File.separator;
    }

    @Override
    protected void invoke(Project project, Editor editor, GoFile file) {
    }

    @Test
    public void testPackage_Case1() throws Throwable { doTest(); }

    @Test
    public void testPackage_Case3() throws Throwable { doTest(); }

    @Test
    public void testImport_Case1() throws Throwable { doTest(); }

    @Test
    public void testImport_Case2() throws Throwable { doTest(); }
}
