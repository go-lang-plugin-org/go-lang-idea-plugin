package ro.redeul.google.go.completion;

public class GoPackageCompletionTestCase extends GoCompletionTestCase {
    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "package";
    }

    public void testPackageInTypeDeclaration() {
        doTestVariants("net.go");
    }

    public void testPackageInExpression() {
        doTestVariants("net.go");
    }

    public void testPackageInFunctionParameter1() {
        doTestVariants("net.go");
    }

    public void testPackageInFunctionParameter2() {
        doTestVariants("net.go");
    }

    public void testPackageInAssignment1() {
        doTestVariants("net.go");
    }

    public void testPackageInAssignment2() {
        doTestVariants("net.go");
    }

    public void testPackageInReturnStatement() {
        doTestVariants("net.go");
    }

    public void testPackageInGoStatement() {
        doTest("net.go");
    }

    public void testPackageInDeferStatement() {
        doTest("net.go");
    }

    public void testPackageInFunctionParameterType() {
        doTestVariants("net.go");
    }

    public void testPackageInFunctionResultType() {
        doTestVariants("net.go");
    }

    public void testPackageInVarDeclaration() {
        doTestVariants("net.go");
    }
}
