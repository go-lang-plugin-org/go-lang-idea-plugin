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
    protected String getBasePath() {
        return super.getBasePath() + File.separator + "toplevel";
    }

    public void testImport$simple_nil() throws Throwable { doTest(); }
    public void testImport$simple_default() throws Throwable { doTest(); }
    public void testImport$simple_current() throws Throwable { doTest(); }
    public void testImport$simple_named() throws Throwable { doTest(); }

    public void testImport$empty() throws Throwable { doTest(); }
    public void testImport$empty_open_para() throws Throwable { doTest(); }
    public void testImport$empty_open_para2() throws Throwable { doTest(); }

    public void testImport$empty2() throws Throwable { doTest(); }

    public void testImport$multiple1() throws Throwable { doTest(); }
    public void testImport$multiple2() throws Throwable { doTest(); }

    public void testImport$with_comments() throws Throwable { doTest(); }

    public void testPackage$empty() throws Throwable { doTest(); }
    public void testPackage$empty_nl() throws Throwable { doTest(); }
    public void testPackage$main() throws Throwable { doTest(); }
    public void testPackage$semi() throws Throwable { doTest(); }

    public void testPackage$error1() throws Throwable { doTest(); }
    public void testPackage$error2() throws Throwable { doTest(); }

    public void testComment$case1() throws Throwable { doTest(); }
    public void testComment$case2() throws Throwable { doTest(); }
    public void testComment$case3() throws Throwable { doTest(); }
    public void testComment$case4() throws Throwable { doTest(); }
    public void testComment$case5() throws Throwable { doTest(); }
    public void testComment$case6() throws Throwable { doTest(); }
    public void testComment$case7() throws Throwable { doTest(); }
    public void testComment$case8() throws Throwable { doTest(); }
    public void testComment$case9() throws Throwable { doTest(); }
    public void testComment$case10() throws Throwable { doTest(); }

    public void testConst$case1() throws Throwable { doTest(); }
    public void testConst$case2() throws Throwable { doTest(); }
    public void testConst$case3() throws Throwable { doTest(); }
    public void testConst$case7() throws Throwable { doTest(); }

    public void testVar$case1() throws Throwable { doTest(); }
    public void testVar$case2() throws Throwable { doTest(); }
    public void testVar$case3() throws Throwable { doTest(); }
    public void testVar$case4() throws Throwable { doTest(); }
    public void testVar$case5() throws Throwable { doTest(); }
    public void testVar$case6() throws Throwable { doTest(); }
    public void testVar$case7() throws Throwable { doTest(); }

    public void testType$case1() throws Throwable { doTest(); }
    public void testType$case2() throws Throwable { doTest(); }
    public void testType$case3() throws Throwable { doTest(); }

    public void testFunction$case1() throws Throwable { doTest(); }
    public void testFunction$case2() throws Throwable { doTest(); }
    public void testFunction$case3() throws Throwable { doTest(); }
    public void testFunction$case4() throws Throwable { doTest(); }
    public void testFunction$case5() throws Throwable { doTest(); }
    public void testFunction$case6() throws Throwable { doTest(); }
    public void testFunction$case7() throws Throwable { doTest(); }
    public void testFunction$case8() throws Throwable { doTest(); }
    public void testFunction$case9() throws Throwable { doTest(); }
    public void testFunction$case10() throws Throwable { doTest(); }
    public void testFunction$case11() throws Throwable { doTest(); }
    public void testFunction$case12() throws Throwable { doTest(); }
    public void testFunction$case13() throws Throwable { doTest(); }
    public void testFunction$case14() throws Throwable { doTest(); }
    public void testFunction$case15() throws Throwable { doTest(); }
    public void testFunction$case16() throws Throwable { doTest(); }
    public void testFunction$case17() throws Throwable { doTest(); }
    public void testFunction$case18() throws Throwable { doTest(); }
    public void testFunction$case19() throws Throwable { doTest(); }

    public void testMethod$case1() throws Throwable { doTest(); }
    public void testMethod$case2() throws Throwable { doTest(); }
    public void testMethod$case3() throws Throwable { doTest(); }
    public void testMethod$case4() throws Throwable { doTest(); }

}