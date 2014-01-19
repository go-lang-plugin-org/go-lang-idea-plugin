package ro.redeul.google.go.parsing;

import ro.redeul.google.go.GoParsingTestCase;

/**
 * <p/>
 * Created on Jan-02-2014 15:51
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoTopLevelParsingTestCase extends GoParsingTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + "toplevel";
    }

    public void testComment_case1() throws Exception { _test(); }
    public void testComment_case2() throws Exception { _test(); }
    public void testComment_case3() throws Exception { _test(); }
    public void testComment_case4() throws Exception { _test(); }
    public void testComment_case5() throws Exception { _test(); }
    public void testComment_case6() throws Exception { _test(); }
    public void testComment_case7() throws Exception { _test(); }
    public void testComment_case8() throws Exception { _test(); }
    public void testComment_case9() throws Exception { _test(); }
    public void testComment_case10() throws Exception { _test(); }


    public void testConst_case1() throws Exception { _test(); }
    public void testConst_case2() throws Exception { _test(); }
    public void testConst_case3() throws Exception { _test(); }
    public void testConst_case4() throws Exception { _test(); }
    public void testConst_case7() throws Exception { _test(); }
    public void testConst_oneLiners() throws Exception { _test(); }
    public void testConst_splitAtComma() throws Exception { _test(); }

    public void testPackage_comments() throws Exception { _test(); }
    public void testPackage_declaration() throws Exception { _test(); }
    public void testPackage_error1() throws Exception { _test(); }
    public void testPackage_error2() throws Exception { _test(); }
    public void testPackage_keyword() throws Exception { _test(); }
    public void testPackage_keywordNewLine() throws Exception { _test(); }
    public void testPackage_main() throws Exception { _test(); }
    public void testPackage_withSemi() throws Exception { _test(); }

    public void testVar_comments() throws Exception { _test(); }
    public void testVar_initialization() throws Exception { _test(); }
    public void testVar_multiple() throws Exception { _test(); }
    public void testVar_simple1() throws Exception { _test(); }
    public void testVar_simple2() throws Exception { _test(); }
    public void testVar_simpleWithComments() throws Exception { _test(); }
    public void testVar_multipleOneLine() throws Exception { _test(); }
    public void testVar_multipleOneLineWithInitialization() throws Exception { _test(); }
    public void testVar_multipleSplitAtComma() throws Exception { _test(); }
}
