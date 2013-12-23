package ro.redeul.google.go;

import com.intellij.testFramework.PsiTestCase;
import org.junit.Ignore;
import ro.redeul.google.go.lang.psi.GoFile;

@Ignore
public abstract class GoPsiTestCase extends PsiTestCase {

    protected String getTestDataPath() {
        return "testdata/" + getTestDataRelativePath();
    }

    protected String getTestDataRelativePath() {
        return "";
    }

    protected  GoFile parse(String content) throws Exception {
        return (GoFile)createFile("temp.go", content);
    }

}
