package ro.redeul.google.go.lang.parser;

import java.io.File;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 5, 2010
 * Time: 9:56:52 PM
 */
public class GoTopLevelTestCase extends GoParsingTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + File.separator + "toplevel";
    }

    public void testImport_simpleNil() throws Throwable { doTest(); }
    public void testImport_simpleDefault() throws Throwable { doTest(); }
    public void testImport_simpleCurrent() throws Throwable { doTest(); }
    public void testImport_simpleNamed() throws Throwable { doTest(); }

    public void testImport_empty() throws Throwable { doTest(); }
    public void testImport_emptyOpenPara() throws Throwable { doTest(); }
    public void testImport_emptyOpenPara2() throws Throwable { doTest(); }

    public void testImport_empty2() throws Throwable { doTest(); }

    public void testImport_multiple1() throws Throwable { doTest(); }
    public void testImport_multiple2() throws Throwable { doTest(); }

    public void testImport_withComments() throws Throwable { doTest(); }

    public void testPackage_empty() throws Throwable { doTest(); }
    public void testPackage_emptyNl() throws Throwable { doTest(); }
    public void testPackage_main() throws Throwable { doTest(); }
    public void testPackage_semi() throws Throwable { doTest(); }

    public void testPackage_error1() throws Throwable { doTest(); }
    public void testPackage_error2() throws Throwable { doTest(); }

    public void testComment_case1() throws Throwable { doTest(); }
    public void testComment_case2() throws Throwable { doTest(); }
    public void testComment_case3() throws Throwable { doTest(); }
    public void testComment_case4() throws Throwable { doTest(); }
    public void testComment_case5() throws Throwable { doTest(); }
    public void testComment_case6() throws Throwable { doTest(); }
    public void testComment_case7() throws Throwable { doTest(); }
    public void testComment_case8() throws Throwable { doTest(); }
    public void testComment_case9() throws Throwable { doTest(); }
    public void testComment_case10() throws Throwable { doTest(); }

    public void testConst_case1() throws Throwable { doTest(); }
    public void testConst_case2() throws Throwable { doTest(); }
    public void testConst_case3() throws Throwable { doTest(); }
    public void testConst_case7() throws Throwable { doTest(); }

    public void testVar_case1() throws Throwable { doTest(); }
    public void testVar_case2() throws Throwable { doTest(); }
    public void testVar_case3() throws Throwable { doTest(); }
    public void testVar_case4() throws Throwable { doTest(); }
    public void testVar_case5() throws Throwable { doTest(); }
    public void testVar_case6() throws Throwable { doTest(); }
    public void testVar_case7() throws Throwable { doTest(); }

    public void testType_case1() throws Throwable { doTest(); }
    public void testType_case2() throws Throwable { doTest(); }
    public void testType_case3() throws Throwable { doTest(); }

    public void testFunction_case1() throws Throwable { doTest(); }
    public void testFunction_case2() throws Throwable { doTest(); }
    public void testFunction_case3() throws Throwable { doTest(); }
    public void testFunction_case4() throws Throwable { doTest(); }
    public void testFunction_case5() throws Throwable { doTest(); }
    public void testFunction_case6() throws Throwable { doTest(); }
    public void testFunction_case7() throws Throwable { doTest(); }
    public void testFunction_case8() throws Throwable { doTest(); }
    public void testFunction_case9() throws Throwable { doTest(); }
    public void testFunction_case10() throws Throwable { doTest(); }
    public void testFunction_case11() throws Throwable { doTest(); }
    public void testFunction_case12() throws Throwable { doTest(); }
    public void testFunction_case13() throws Throwable { doTest(); }
    public void testFunction_case14() throws Throwable { doTest(); }
    public void testFunction_case15() throws Throwable { doTest(); }
    public void testFunction_case16() throws Throwable { doTest(); }
    public void testFunction_case17() throws Throwable { doTest(); }
    public void testFunction_case18() throws Throwable { doTest(); }
    public void testFunction_case19() throws Throwable { doTest(); }

    public void testMethod_case1() throws Throwable { doTest(); }
    public void testMethod_case2() throws Throwable { doTest(); }
    public void testMethod_case3() throws Throwable { doTest(); }
    public void testMethod_case4() throws Throwable { doTest(); }

}