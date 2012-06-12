package ro.redeul.google.go.psi;

import com.intellij.testFramework.PsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.util.GoTestUtils;

public abstract class GoPsiTestCase extends PsiTestCase {

    protected String getTestDataPath() {
        return GoTestUtils.getTestDataPath() + getTestDataRelativePath();
    }

    protected String getTestDataRelativePath() {
        return "";
    }

    protected  GoFile parse(String content) throws Exception {
        return (GoFile)createFile("temp.go", content);
    }
}
