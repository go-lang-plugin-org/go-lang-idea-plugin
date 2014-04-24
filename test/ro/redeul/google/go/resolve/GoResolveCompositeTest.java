package ro.redeul.google.go.resolve;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveCompositeTest extends GoPsiResolveTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "composite/";
    }

    public void testTypeName() throws Exception {
        doTest();
    }

    public void testTypeStruct() throws Exception {
        doTest();
    }

    public void testTypeStructArray() throws Exception {
        doTest();
    }

    public void testTypeStructSlice() throws Exception {
        doTest();
    }

    public void testTypeStructMap() throws Exception {
        doTest();
    }

    public void testTypeNameArray() throws Exception {
        doTest();
    }

    public void testTypeNameMap() throws Exception {
        doTest();
    }

    public void testTypeNameSlice() throws Exception {
        doTest();
    }

    public void testNestedStruct() throws Exception {
        doTest();
    }

    public void testNestedArrayStruct() throws Exception {
        doTest();
    }

    public void testKeyAsConstantExpression() throws Exception {
        doTest();
    }

    public void testExpressionKey() throws Exception {
        doTest();
    }

    public void testPromotedAnonymousField1() throws Exception {
        doTest();
    }

    public void testPromotedAnonymousField2() throws Exception {
        doTest();
    }

    public void testPromotedAnonymousField3() throws Exception {
        doTest();
    }

    public void testPromotedAnonymousField4() throws Exception {
        doTest();
    }

    public void testTypeSwitch() throws Exception {
        doTest();
    }
}
