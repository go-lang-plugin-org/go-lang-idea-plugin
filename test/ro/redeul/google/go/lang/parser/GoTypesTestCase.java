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

//    @Test public void testLiteralsInteger() throws Throwable { doTest(); }
//    @Test public void testLiteralsFloat() throws Throwable { doTest(); }
//    @Test public void testLiteralsImaginary() throws Throwable { doTest(); }
//    @Test public void testLiteralsChar() throws Throwable { doTest(); }
//    @Test public void testLiteralsString() throws Throwable { doTest(); }
//    @Test public void testLiteralsCompositeStruct() throws Throwable { doTest(); }
//    @Test public void testLiteralsCompositeStruct_nested() throws Throwable { doTest(); }
//    @Test public void testLiteralsCompositeSlice() throws Throwable { doTest(); }
//    @Test public void testLiteralsCompositeArray() throws Throwable { doTest(); }
//    @Test public void testLiteralsCompositeArray_implicit_length() throws Throwable { doTest(); }
//    @Test public void testLiteralsCompositeMap() throws Throwable { doTest(); }
//    @Test public void testLiteralsCompositeType() throws Throwable { doTest(); }
//    @Test public void testLiteralsFunction() throws Throwable { doTest(); }
//    @Test public void testLiteralsCompositeStruct_nested() throws Throwable { doTest(); }

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
}
