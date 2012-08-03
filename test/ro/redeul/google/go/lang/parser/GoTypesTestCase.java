package ro.redeul.google.go.lang.parser;

import java.io.File;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 5, 2010
 * Time: 9:56:52 PM
 */
public class GoTypesTestCase extends GoParsingTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + File.separator + "types";
    }

    public void testFunctions_func1() throws Throwable { doTest(); }
    public void testFunctions_func2() throws Throwable { doTest(); }
    public void testFunctions_func3() throws Throwable { doTest(); }
    public void testFunctions_func4() throws Throwable { doTest(); }
    public void testFunctions_func5() throws Throwable { doTest(); }
    public void testFunctions_func6() throws Throwable { doTest(); }
    public void testFunctions_func7() throws Throwable { doTest(); }
    public void testFunctions_func8() throws Throwable { doTest(); }
    public void testFunctions_func9() throws Throwable { doTest(); }

    public void testChannel_bidi() throws Throwable { doTest(); }
    public void testChannel_send() throws Throwable { doTest(); }
    public void testChannel_recv() throws Throwable { doTest(); }
    public void testChannel_invalid() throws Throwable { doTest(); }

    public void testMap_case1() throws Throwable { doTest(); }
    public void testMap_case2() throws Throwable { doTest(); }
    public void testMap_case3() throws Throwable { doTest(); }
    public void testMap_case4Func1() throws Throwable { doTest(); }
    public void testMap_case4Func2() throws Throwable { doTest(); }

    public void testArray_case1() throws Throwable { doTest(); }
    public void testArray_case2() throws Throwable { doTest(); }
    public void testArray_case3() throws Throwable { doTest(); }
    public void testArray_case4() throws Throwable { doTest(); }
    public void testArray_case5() throws Throwable { doTest(); }

    public void testStruct_empty() throws Throwable { doTest(); }
    public void testStruct_oneField() throws Throwable { doTest(); }
    public void testStruct_twoFields() throws Throwable { doTest(); }
    public void testStruct_twoFields2() throws Throwable { doTest(); }
    public void testStruct_nFields() throws Throwable { doTest(); }
    public void testStruct_embedded() throws Throwable { doTest(); }
    public void testStruct_error() throws Throwable { doTest(); }

    public void testInterface_case1() throws Throwable { doTest(); }
    public void testInterface_case2() throws Throwable { doTest(); }
    public void testInterface_case3() throws Throwable { doTest(); }
    public void testInterface_incomplete1() throws Throwable { doTest(); }


}
