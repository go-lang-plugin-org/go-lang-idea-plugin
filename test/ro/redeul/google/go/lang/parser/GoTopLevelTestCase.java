package ro.redeul.google.go.lang.parser;

import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 5, 2010
 * Time: 9:56:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTopLevelTestCase extends GoParsingTestCase {

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "toplevel";
    }

    @Test public void testImportSimple_nil() throws Throwable { doTest(); }
    @Test public void testImportSimple_default() throws Throwable { doTest(); }
    @Test public void testImportSimple_current() throws Throwable { doTest(); }
    @Test public void testImportSimple_named() throws Throwable { doTest(); }

    @Test public void testImportEmpty() throws Throwable { doTest(); }
    @Test public void testImportEmpty_open_para() throws Throwable { doTest(); }
    @Test public void testImportEmpty_open_para2() throws Throwable { doTest(); }

    @Test public void testImportEmpty2() throws Throwable { doTest(); }

    @Test public void testImportMultiple1() throws Throwable { doTest(); }
    @Test public void testImportMultiple2() throws Throwable { doTest(); }

    @Test public void testImportWith_comments() throws Throwable { doTest(); }

    @Test public void testPackageEmpty() throws Throwable { doTest(); }
    @Test public void testPackageEmpty_nl() throws Throwable { doTest(); }
    @Test public void testPackageMain() throws Throwable { doTest(); }
    @Test public void testPackageSemi() throws Throwable { doTest(); }

    @Test public void testCommentCase1() throws Throwable { doTest(); }
    @Test public void testCommentCase2() throws Throwable { doTest(); }
    @Test public void testCommentCase3() throws Throwable { doTest(); }
    @Test public void testCommentCase4() throws Throwable { doTest(); }
    @Test public void testCommentCase5() throws Throwable { doTest(); }
    @Test public void testCommentCase6() throws Throwable { doTest(); }
    @Test public void testCommentCase7() throws Throwable { doTest(); }
    @Test public void testCommentCase8() throws Throwable { doTest(); }
    @Test public void testCommentCase9() throws Throwable { doTest(); }
    @Test public void testCommentCase10() throws Throwable { doTest(); }

    @Test public void testFunctionCase1() throws Throwable { doTest(); }
    @Test public void testFunctionCase2() throws Throwable { doTest(); }
    @Test public void testFunctionCase3() throws Throwable { doTest(); }
    @Test public void testFunctionCase4() throws Throwable { doTest(); }
    @Test public void testFunctionCase5() throws Throwable { doTest(); }
    @Test public void testFunctionCase6() throws Throwable { doTest(); }
    @Test public void testFunctionCase7() throws Throwable { doTest(); }
    @Test public void testFunctionCase8() throws Throwable { doTest(); }
    @Test public void testFunctionCase9() throws Throwable { doTest(); }
    @Test public void testFunctionCase10() throws Throwable { doTest(); }
    @Test public void testFunctionCase11() throws Throwable { doTest(); }
    @Test public void testFunctionCase12() throws Throwable { doTest(); }
    @Test public void testFunctionCase13() throws Throwable { doTest(); }
    @Test public void testFunctionCase14() throws Throwable { doTest(); }
    @Test public void testFunctionCase15() throws Throwable { doTest(); }
    @Test public void testFunctionCase16() throws Throwable { doTest(); }
    @Test public void testFunctionCase17() throws Throwable { doTest(); }
    @Test public void testFunctionCase18() throws Throwable { doTest(); }

}