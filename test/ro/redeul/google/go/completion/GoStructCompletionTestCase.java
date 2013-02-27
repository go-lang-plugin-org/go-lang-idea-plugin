package ro.redeul.google.go.completion;

public class GoStructCompletionTestCase extends GoCompletionTestCase{
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "struct";
    }

    public void testStructMembers() {
        doTestVariants();
    }

    public void testAnonymousStructMembers() {
        doTestVariants();
    }

    public void testPromotedFieldStructMembers() {
        doTestVariants();
    }

    public void testMembersOfAnonymousField() {
        doTestVariants();
    }

    public void testMemberOfTypePointerCompletion() {
        doTestVariants();
    }

    public void testPromotedFields() {
        doTestVariants();
    }

    public void testRecursiveFields() {
        doTestVariants();
    }

    public void testMethodsOfTypePointerCompletion(){
        doTestVariants();
    }
}
