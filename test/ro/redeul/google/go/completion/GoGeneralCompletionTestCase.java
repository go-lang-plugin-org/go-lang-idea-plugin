/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.completion;

public class GoGeneralCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "general";
    }

    public void testHandleNewBuiltinFunction() {
        doTestVariants();
    }
}
