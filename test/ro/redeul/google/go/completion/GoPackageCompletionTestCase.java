package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoPackageCompletionTestCase extends GoCompletionTestCase {
    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "package";
    }

    public void testPackageInTypeDeclaration() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInExpression() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInFunctionParameter1() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInFunctionParameter2() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInAssignment1() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInAssignment2() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInReturnStatement() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInGoStatement() throws IOException {
        doTest("net.go");
    }

    public void testPackageInDeferStatement() throws IOException {
        doTest("net.go");
    }

    public void testPackageInFunctionParameterType() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInFunctionResultType() throws IOException {
        doTestVariants("net.go");
    }

    public void testPackageInVarDeclaration() throws IOException {
        doTestVariants("net.go");
    }
}
