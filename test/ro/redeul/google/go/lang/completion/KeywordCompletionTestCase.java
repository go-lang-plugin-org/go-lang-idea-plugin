package ro.redeul.google.go.lang.completion;

import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 20, 2010
 * Time: 5:29:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class KeywordCompletionTestCase extends GoCompletionTestBase {
    @Override
    protected String getRelativeDataPath() {
        return super.getRelativeDataPath() + File.separator + "keywords";
    }
    
    @Test(enabled = false)
    public void testPackageCase1() throws Throwable { doBasicTest(); }

    @Test(enabled = false)
    public void testPackageCase3() throws Throwable { doBasicTest(); }

    @Test(enabled = false)
    public void testImportCase1() throws Throwable { doBasicTest(); }

    @Test(enabled = false)
    public void testImportCase2() throws Throwable { doBasicTest(); }
}
