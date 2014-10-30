/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoCompletionEmbeddedInterfaceTestCase extends GoCompletionTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "embedded/";
    }

    public void testEmbeddedInterface() throws IOException {
        _testVariants();
    }

    public void testEmbeddedInterfaceViaPointer() throws IOException {
        _testVariants();
    }
}
