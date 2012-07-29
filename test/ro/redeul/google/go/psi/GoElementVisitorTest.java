package ro.redeul.google.go.psi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import ro.redeul.google.go.GoPsiTestCase;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIndexExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoTestUtils;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 6/5/12
 */
public class GoElementVisitorTest extends GoPsiTestCase {

    @Override
    protected String getTestDataRelativePath() {
        return "psi/visitor";
    }

    public void testSimple() throws Throwable {
        doTest();
    }

    public void testIndexExpressions() throws Throwable {
        doTest();
    }

//    public void testSliceExpressions() throws Throwable {
//        doTest();
//    }

    public void doTest() throws IOException {
        doTest(getTestName(true).replace('$', '/') + ".test");
    }

    private void doTest(String fileName) throws IOException {
        final List<String> list =
            GoTestUtils.readInput(getTestDataPath() + "/" + fileName);

        if (list.size() != 3) {
            Assert.fail("invalid test case file");
        }

        GoFile goFile = (GoFile)
            GoTestUtils.createPseudoPhysicalGoFile(getProject(), list.get(0));

        GoRecursiveCollectorVisitor visitor = visitorForElementType(list.get(1));
        visitor.visitElement(goFile);

        List<GoPsiElement> elements = visitor.getElements();

        StringBuilder builder = new StringBuilder();
        for (GoPsiElement element : elements) {
            builder.append(element.getText()).append("\n");
        }

        Assert.assertEquals(builder.toString().trim(), list.get(2));
    }

    private GoRecursiveCollectorVisitor visitorForElementType(String elemType) {

        if (elemType.equals("GoIdentifier")) {
            return new GoRecursiveCollectorVisitor() {
                @Override
                public void visitLiteralIdentifier(
                    GoLiteralIdentifier identifier) {
                    elements.add(identifier);
                }
            };
        }

        if (elemType.equals("GoIndexExpression")) {
            return new GoRecursiveCollectorVisitor() {
                @Override
                public void visitIndexExpression(GoIndexExpression expression) {
                    elements.add(expression);
                }
            };
        }

        return new GoRecursiveCollectorVisitor();
    }

    class GoRecursiveCollectorVisitor extends GoRecursiveElementVisitor {
        List<GoPsiElement> elements = new ArrayList<GoPsiElement>();

        public List<GoPsiElement> getElements() {
            return elements;
        }
    }
}
