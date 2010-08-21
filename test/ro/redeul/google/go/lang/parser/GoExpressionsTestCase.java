package ro.redeul.google.go.lang.parser;

import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 5, 2010
 * Time: 9:56:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoExpressionsTestCase extends GoParsingTestCase {

    @Override
    protected String getBasePath() {
        return super.getBasePath() + "expressions";
    }

    @Test public void testCall_or_conversionSimple() throws Throwable { doTest(); }

    @Test public void testCall_or_conversionParameter() throws Throwable { doTest(); }

    @Test public void testCall_or_conversionParameters() throws Throwable { doTest(); }

    @Test public void testCall_or_conversionParenthesised() throws Throwable { doTest(); }

    @Test public void testIndexSimple() throws Throwable { doTest(); }

    @Test public void testIndexExpression() throws Throwable { doTest(); }

    @Test public void testSliceSimple() throws Throwable { doTest(); }

    @Test public void testSliceExpressions() throws Throwable { doTest(); }

    @Test public void testType_assertionSimple() throws Throwable { doTest(); }
    
    @Test public void testType_assertionType() throws Throwable { doTest(); }

    @Test public void testSelectorSimple() throws Throwable { doTest(); }

    @Test public void testSelectorDouble() throws Throwable { doTest(); }

    @Test public void testParenthesisedBasic() throws Throwable { doTest(); }

//    @Test public void testParenthesisedPointer_type() throws Throwable { doTest(); }

    @Test public void testLiteralIdentifier() throws Throwable { doTest(); }

    @Test public void testLiteralInteger() throws Throwable { doTest(); }

    @Test public void testLiteralFloat() throws Throwable { doTest(); }

    @Test public void testLiteralImaginary() throws Throwable { doTest(); }

    @Test public void testLiteralChar() throws Throwable { doTest(); }

    @Test public void testLiteralString() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeError1() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeBuiltins() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeStruct() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeStruct_nested() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeSlice() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeArray() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeArray_implicit_length() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeMap() throws Throwable { doTest(); }

    @Test public void testLiteralCompositeType() throws Throwable { doTest(); }

    @Test public void testLiteralFunction() throws Throwable { doTest(); }

    @Test public void testPrimaryPrimary1() throws Throwable { doTest(); }

    @Test public void testPrimaryPrimary2() throws Throwable { doTest(); }

    @Test public void testPrimaryPrimary3() throws Throwable { doTest(); }

    @Test public void testPrimaryPrimary4() throws Throwable { doTest(); }

    @Test public void testPrimaryPrimary5() throws Throwable { doTest(); }

    @Test public void testPrimaryPrimary6() throws Throwable { doTest(); }

    @Test public void testPrimaryPrimary7() throws Throwable { doTest(); }

    @Test public void testPrimaryPrimary8() throws Throwable { doTest(); }

    @Test public void testBinarySimpleAdditive() throws Throwable { doTest(); }

    @Test public void testBinarySimpleMultiplicative() throws Throwable { doTest(); }

    @Test public void testBinarySimpleRelational() throws Throwable { doTest(); }

    @Test public void testBinarySimpleCommunication() throws Throwable { doTest(); }

    @Test public void testBinarySimpleLogical_and() throws Throwable { doTest(); }

    @Test public void testBinarySimpleLogical_or() throws Throwable { doTest(); }

    @Test public void testBinaryNestedAdditive() throws Throwable { doTest(); }

    @Test public void testBinaryNestedPrecedence1() throws Throwable { doTest(); }

    @Test public void testBuiltinMake_channel() throws Throwable { doTest(); }

    @Test public void testBuiltinLiterals() throws Throwable { doTest(); }

//    @Test public void testLiteralsCompositeStruct_nested() throws Throwable { doTest(); }

}
