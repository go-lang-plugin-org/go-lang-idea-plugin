package ro.redeul.google.go.completion;

import java.io.IOException;

public class GoPackageCompletionTestCase extends GoCompletionTestCase {
    @Override
    protected String getTestDataRelativePath() {
        return super.getTestDataRelativePath() + "package";
    }

    public void testPackageInTypeDeclaration() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInExpression() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInFunctionParameter1() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInFunctionParameter2() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInAssignment1() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInAssignment2() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInReturnStatement() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInGoStatement() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInDeferStatement() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInFunctionParameterType() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInFunctionResultType() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }

    public void testPackageInVarDeclaration() throws IOException {
        addPackage("net", "net.go");
        _testVariants();
    }
}
