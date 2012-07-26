package ro.redeul.google.go.highlight;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoLightCodeInsightFixtureTestCase;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.util.GoTestUtils;

import java.io.File;

import static com.intellij.psi.util.PsiTreeUtil.nextLeaf;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class GoReadWriteAccessDetectorTest extends GoLightCodeInsightFixtureTestCase {
    public void testNormal() throws Exception { doTest(); }

    protected void doTest() throws Exception {
        detect(parse(new String(FileUtil.loadFileText(new File(getTestFileName())))));
    }

    // GoRecursiveElementVisitor cannot visit comments,
    // that's why I wrote this recursive function to walk through elements.
    private void detect(PsiElement element) {
        GoReadWriteAccessDetector detector = new GoReadWriteAccessDetector();
        PsiElement child = element.getFirstChild();
        while (child != null) {
            if (isNodeOfType(child, GoElementTypes.COMMENTS) && GoTestUtils.MARKER_BEGIN.equals(child.getText())) {
                PsiElement toDetect = findParentOfType(nextLeaf(child), GoLiteralIdentifier.class);
                assertNotNull(getElementInfo(child, "Should be followed by identifier"), toDetect);
                PsiElement endComment = nextLeaf(toDetect);
                assertNotNull(endComment);

                String text = endComment.getText();
                assertTrue(isNodeOfType(endComment, GoElementTypes.COMMENTS) );
                assertTrue(endComment.getText().startsWith("/*end."));
                assertTrue(getElementInfo(toDetect, "Should be accessible"), detector.isReadWriteAccessible(toDetect));

                String expected = text.substring(6, text.length() - 2);
                String access = detector.getExpressionAccess(toDetect).toString();
                assertEquals(getElementInfo(toDetect, "Type error!"), expected, access);

            } else {
                detect(child);
            }
            child = child.getNextSibling();
        }
    }

    private String getElementInfo(PsiElement element, String msg) {
        Document document = myFixture.getEditor().getDocument();
        int offset = element.getTextOffset();
        int row = document.getLineNumber(offset);
        int col = offset - document.getLineStartOffset(row);
        return String.format("Element \"%s\" at (%d,%d): %s", element.getText(), row + 1, col + 1, msg);
    }

    @Override
    protected String getTestDataRelativePath() {
        return "highlight/readWriteAccessDetector/";
    }
}
