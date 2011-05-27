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
    protected String getBasePath() {
        return super.getBasePath() + File.separator + "types";
    }

    public void testFunctions$func1() throws Throwable { doTest(); }
    public void testFunctions$func2() throws Throwable { doTest(); }
    public void testFunctions$func3() throws Throwable { doTest(); }
    public void testFunctions$func4() throws Throwable { doTest(); }
    public void testFunctions$func5() throws Throwable { doTest(); }
    public void testFunctions$func6() throws Throwable { doTest(); }
    public void testFunctions$func7() throws Throwable { doTest(); }
    public void testFunctions$func8() throws Throwable { doTest(); }
    public void testFunctions$func9() throws Throwable { doTest(); }

    public void testChannel$bidi() throws Throwable { doTest(); }
    public void testChannel$send() throws Throwable { doTest(); }
    public void testChannel$recv() throws Throwable { doTest(); }

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

    public void testMap$case1() throws Throwable { doTest(); }
    public void testMap$case2() throws Throwable { doTest(); }
    public void testMap$case3() throws Throwable { doTest(); }

    public void testArray$case1() throws Throwable { doTest(); }
    public void testArray$case2() throws Throwable { doTest(); }
    public void testArray$case3() throws Throwable { doTest(); }
    public void testArray$case4() throws Throwable { doTest(); }
    public void testArray$case5() throws Throwable { doTest(); }

    public void testStruct$empty() throws Throwable { doTest(); }
    public void testStruct$one_field() throws Throwable { doTest(); }
    public void testStruct$two_fields() throws Throwable { doTest(); }
    public void testStruct$two_fields2() throws Throwable { doTest(); }
    public void testStruct$n_fields() throws Throwable { doTest(); }
}
