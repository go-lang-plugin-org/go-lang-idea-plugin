/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.completion;

public class GoCompletionEmbeddedInterfaceTestCase extends GoCompletionTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "embedded/";
    }

    public void testEmbeddedInterface() {
        doTestVariants();
    }

    public void testEmbeddedInterfaceViaPointer() {
        doTestVariants();
    }
}
