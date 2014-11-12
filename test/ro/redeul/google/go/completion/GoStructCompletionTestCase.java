package ro.redeul.google.go.completion;


import org.junit.Ignore;

import java.io.IOException;

public class GoStructCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "struct";
    }

    public void testStructMembers() throws IOException {
        _testVariants();
    }

    public void testAnonymousStructMembers() throws IOException {
        _testVariants();
    }

    public void testPromotedFieldStructMembers() throws IOException {
        _testVariants();
    }

    public void testMembersOfAnonymousField() throws IOException {
        _testVariants();
    }

    public void testMemberOfTypePointerCompletion() throws IOException {
        _testVariants();
    }

    public void testPromotedFields() throws IOException {
        _testVariants();
    }

    @Ignore("broken")
    public void testRecursiveFields() throws IOException {
        _testVariants();
    }

    public void testMethodsOfTypePointerCompletion() throws IOException {
        _testVariants();
    }

    public void testPublicStructMemberFromImported() throws IOException {
        addPackage("package1", "package1/file1.go");
        _testVariants();
    }
}
