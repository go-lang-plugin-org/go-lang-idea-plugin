/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoGeneralCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "general";
    }

    public void testHandleNewBuiltinFunction() throws IOException {
        doTestVariants();
    }

    public void testHandleComplexBuiltinFunction() throws IOException {
        doTestVariants();
    }

    public void testHandleRealBuiltinFunction() throws IOException {
        doTestVariants();
    }

    public void testHandleMakeBuiltinFunction() throws IOException {
        doTestVariants();
    }

    public void testBuiltinFunctionsAtStatementLevel() throws IOException {
        doTestVariants();
    }

    public void testBuiltinFunctionsAtExpressionLevel() throws IOException {
        doTestVariants();
    }
}
