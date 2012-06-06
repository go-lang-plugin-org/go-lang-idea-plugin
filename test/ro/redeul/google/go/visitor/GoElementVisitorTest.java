package ro.redeul.google.go.visitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import junit.framework.Assert;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.TestUtils;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: 6/5/12
 */
public class GoElementVisitorTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getBasePath() {

        return FileUtil.toSystemIndependentName(
            PluginPathManager.getPluginHomePathRelative(
                "google-go-language")) + "/testdata/visitor";
    }

    public void doTest() throws IOException {
        doTest(getTestName(true).replace('$', '/') + ".test");
    }

    private void doTest(String fileName) throws IOException {
        final List<String> list =
            TestUtils.readInput(getTestDataPath() + "/" + fileName);

        if (list.size() != 3) {
            Assert.fail("invalid test case file");
        }

        GoFile goFile = (GoFile)
            TestUtils.createPseudoPhysicalGoFile(getProject(), list.get(0));

        GoRecursiveCollectorVisitor visitor = visitorForElementType(list.get(1));
        visitor.visitElement(goFile);

        List<GoPsiElement> elements = visitor.getElements();

        StringBuilder builder = new StringBuilder();
        for (GoPsiElement element : elements) {
            builder.append(element.getText()).append("\n");
        }

        org.testng.Assert.assertEquals(builder.toString().trim(), list.get(2));
    }

    private GoRecursiveCollectorVisitor visitorForElementType(String elemType) {

        if (elemType.equals("GoIdentifier")) {
            return new GoRecursiveCollectorVisitor() {
                @Override
                public void visitIdentifier(GoIdentifier goIdentifier) {
                    elements.add(goIdentifier);
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

    public void testSimple() throws Throwable {
        doTest();
    }



}
