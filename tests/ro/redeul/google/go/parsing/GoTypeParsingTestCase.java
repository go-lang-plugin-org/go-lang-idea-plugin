package ro.redeul.google.go.parsing;

import ro.redeul.google.go.GoParsingTestCase;

import java.io.File;

/**
 * Types parsing test case.
 *
 * <p/>
 * Created on Jan-12-2014 18:02
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoTypeParsingTestCase extends GoParsingTestCase {

    @Override
    protected String getRelativeTestDataPath() {
        return super.getRelativeTestDataPath() + File.separator + "types";
    }

    public void testArray_case1() throws Throwable { _test(); }
    public void testArray_case2() throws Throwable { _test(); }
    public void testArray_case3() throws Throwable { _test(); }
    public void testArray_case4() throws Throwable { _test(); }
    public void testArray_case5() throws Throwable { _test(); }

    public void testChannel_bidi() throws Throwable { _test(); }
    public void testChannel_invalid() throws Throwable { _test(); }
    public void testChannel_recv() throws Throwable { _test(); }
    public void testChannel_send() throws Throwable { _test(); }

    public void testFunctions_func1() throws Throwable { _test(); }
    public void testFunctions_func2() throws Throwable { _test(); }
    public void testFunctions_func3() throws Throwable { _test(); }
    public void testFunctions_func4() throws Throwable { _test(); }
    public void testFunctions_func5() throws Throwable { _test(); }
    public void testFunctions_func6() throws Throwable { _test(); }
    public void testFunctions_func7() throws Throwable { _test(); }
    public void testFunctions_func8() throws Throwable { _test(); }
    public void testFunctions_func9() throws Throwable { _test(); }

    public void testInterface_case1() throws Throwable { _test(); }
    public void testInterface_case2() throws Throwable { _test(); }
    public void testInterface_case3() throws Throwable { _test(); }
    public void testInterface_incomplete1() throws Throwable { _test(); }

    public void testMap_case1() throws Throwable { _test(); }
    public void testMap_case2() throws Throwable { _test(); }
    public void testMap_case3() throws Throwable { _test(); }
    public void testMap_case4Func1() throws Throwable { _test(); }
    public void testMap_case4Func2() throws Throwable { _test(); }

    public void testStruct_embedded() throws Throwable { _test(); }
    public void testStruct_empty() throws Throwable { _test(); }
    public void testStruct_error() throws Throwable { _test(); }
    public void testStruct_nFields() throws Throwable { _test(); }
    public void testStruct_oneField() throws Throwable { _test(); }
    public void testStruct_twoFields1() throws Throwable { _test(); }
    public void testStruct_twoFields2() throws Throwable { _test(); }



}
