/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoGeneralCompletionTestCase extends GoCompletionTestCase {
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "general";
    }

    public void testHandleNewBuiltinFunction() throws IOException {
        _testVariants();
    }

    public void testHandleComplexBuiltinFunction() throws IOException {
        _testVariants();
    }

    public void testHandleRealBuiltinFunction() throws IOException {
        _testVariants();
    }

    public void testHandleMakeBuiltinFunction() throws IOException {
        _testVariants();
    }

    public void testBuiltinFunctionsAtStatementLevel() throws IOException {
        _testVariants();
    }

    public void testBuiltinFunctionsAtExpressionLevel() throws IOException {
        _testVariants();
    }
}
