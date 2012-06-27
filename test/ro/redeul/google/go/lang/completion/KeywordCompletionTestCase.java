package ro.redeul.google.go.lang.completion;

import java.io.File;

import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 20, 2010
 * Time: 5:29:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class KeywordCompletionTestCase extends GoCompletionTestBase {
    @Override
    protected String getRelativeDataPath() {
        return super.getRelativeDataPath() + File.separator + "keywords";
    }

    @Test()
    public void testPackageCase1() throws Throwable { doBasicTest(); }

    @Test()
    public void testPackageCase3() throws Throwable { doBasicTest(); }

    @Test()
    public void testImportCase1() throws Throwable { doBasicTest(); }

    @Test()
    public void testImportCase2() throws Throwable { doBasicTest(); }
}
