package ro.redeul.google.go.lang.parser;

import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 5, 2010
 * Time: 9:56:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTypesTestCase extends GoParsingTestCase {

    @Override
    protected String getRelativeDataPath() {
        return super.getRelativeDataPath() + File.separator + "types";
    }

    @Test public void testFunctionsFunc1() throws Throwable { doTest(); }

    @Test public void testFunctionsFunc2() throws Throwable { doTest(); }

    @Test public void testFunctionsFunc3() throws Throwable { doTest(); }

    @Test public void testFunctionsFunc4() throws Throwable { doTest(); }

    @Test public void testFunctionsFunc5() throws Throwable { doTest(); }

    @Test public void testFunctionsFunc6() throws Throwable { doTest(); }

    @Test public void testFunctionsFunc7() throws Throwable { doTest(); }

    @Test public void testFunctionsFunc8() throws Throwable { doTest(); }

    @Test public void testFunctionsFunc9() throws Throwable { doTest(); }

    @Test public void testChannelBidi() throws Throwable { doTest(); }

    @Test public void testChannelSend() throws Throwable { doTest(); }

    @Test public void testChannelRecv() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsInteger() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsFloat() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsImaginary() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsChar() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsString() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsCompositeStruct() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsCompositeStruct_nested() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsCompositeSlice() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsCompositeArray() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsCompositeArray_implicit_length() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsCompositeMap() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsCompositeType() throws Throwable { doTest(); }
//
//    @Test public void testLiteralsFunction() throws Throwable { doTest(); }

//    @Test public void testLiteralsCompositeStruct_nested() throws Throwable { doTest(); }

    @Test public void testMapCase1() throws Throwable { doTest(); }
    @Test public void testMapCase2() throws Throwable { doTest(); }
    @Test public void testMapCase3() throws Throwable { doTest(); }

    @Test public void testArrayCase1() throws Throwable { doTest(); }
    @Test public void testArrayCase2() throws Throwable { doTest(); }
    @Test public void testArrayCase3() throws Throwable { doTest(); }
    @Test public void testArrayCase4() throws Throwable { doTest(); }
    @Test public void testArrayCase5() throws Throwable { doTest(); }
}
