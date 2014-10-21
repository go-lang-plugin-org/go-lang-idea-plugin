package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoStructCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "struct";
    }

    public void testStructMembers() throws IOException {
        doTestVariants();
    }

    public void testAnonymousStructMembers() throws IOException {
        doTestVariants();
    }

    public void testPromotedFieldStructMembers() throws IOException {
        doTestVariants();
    }

    public void testMembersOfAnonymousField() throws IOException {
        doTestVariants();
    }

    public void testMemberOfTypePointerCompletion() throws IOException {
        doTestVariants();
    }

    public void testPromotedFields() throws IOException {
        doTestVariants();
    }

    public void testRecursiveFields() throws IOException {
        doTestVariants();
    }

    public void testMethodsOfTypePointerCompletion() throws IOException {
        doTestVariants();
    }

    public void testPublicStructMemberFromImported() throws IOException {
        doTestVariants();
    }
}
