package ro.redeul.google.go.lang.completion;

import com.intellij.testFramework.TestDataPath;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 20, 2010
 * Time: 5:29:50 PM
 * To change this template use File | Settings | File Templates.
 */
@TestDataPath("$PROJECT_ROOT/testdata")
public class KeywordCompletionTestCase extends GoCompletionTestBase {

    @Override
    protected String getTestsRelativePath() {
        return "keywords" + File.separator;
    }
    
    @Test
    public void testP() throws Throwable { doBasicTest(); }

}
