package ro.redeul.google.go.resolve;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 8, 2010
 * Time: 2:59:17 PM
 */
public class GoResolveCallsTest extends GoPsiResolveTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "calls/";
    }

    public void testCallToLocalMethodByPointer() throws Exception {
        doTest();
    }

    public void testCallToLocalMethod() throws Exception {
        doTest();
    }

    public void testCallToLocalMethodNested() throws Exception {
        doTest();
    }

    public void testCallToLocalMethodViaMap() throws Exception {
        doTest();
    }

    public void testCallToLocalMethodViaShortVarDeclaration() throws Exception {
        doTest();
    }

    public void testCallToLocalMethodViaSlice() throws Exception {
        doTest();
    }

    public void testCallToLocalMethodViaTypeAssert() throws Exception {
        doTest();
    }

    public void testCallToLocalMethodNestedStop() throws Exception {
        doTest();
    }

    public void testCallToLocalInterfaceMethod() throws Exception {
        doTest();
    }

    public void testCallToLocalInterfaceMethodNested() throws Exception {
        doTest();
    }

    public void testCallToLocalInterfaceMethodViaMap() throws Exception {
        doTest();
    }

    public void testCallToLocalInterfaceMethodViaSlice() throws Exception {
        doTest();
    }

    public void testCallToLocalInterfaceMethodViaTypeAssert() throws Exception {
        doTest();
    }

    public void testCallToLocalInterfaceMethodNestedStop() throws Exception {
        doTest();
    }

    public void testCallToLocalFunction() throws Exception {
        doTest();
    }

    public void testTypeConversionToLocalType() throws Exception {
        doTest();
    }

    public void testConversionToImportedType() throws Exception {
        doTest();
    }

    public void testConversionToLocallyImportedType() throws Exception {
        doTest();
    }

    public void testNoConversionToBlankImportedType() throws Exception {
        doTest();
    }

    public void testConversionToImportedFunction() throws Exception {
        doTest();
    }

    public void testRecursiveMethodCall() throws Exception {
        doTest();
    }

    public void testCallToMethodParameter() throws Exception {
        doTest();
    }

    public void testFunctionInSamePackageDifferentFile() throws Exception {
        doTest();
    }

    public void testCallToFunctionVariable() throws Exception {
        doTest();
    }

    public void testDirectlyInheritedMethodSet() throws Exception {
        doTest();
    }

    public void testGrandParentDirectlyInheritedMethodSet() throws Exception {
        doTest();
    }

    public void testCallToMethodViaShortVar() throws Exception {
        doTest();
    }

    public void testCallToEmbeddedInterfaceMethod() throws Exception {
        doTest();
    }

    @Ignore("Not supporting relative package references yet.")
    public void testRelativePackageReference() throws Exception {
        doTest();
    }

    @Ignore("Not supporting relative package references yet.")
    public void testRelativePackageReferenceDeep() throws Exception {
        doTest();
    }
//    public void testConversionToLocallyImportedType() throws Exception {
//        _testSingleCompletion();
//    }
//
//    public void testNoConversionToBlankImportedType() throws Exception {
//        _testSingleCompletion();
//    }
}
