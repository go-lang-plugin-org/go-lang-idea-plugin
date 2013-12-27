package ro.redeul.google.go.usage;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import ro.redeul.google.go.GoFileBasedPsiTestCase;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.intellij.psi.util.PsiTreeUtil.nextLeaf;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class GoUsageTest extends GoFileBasedPsiTestCase {

    public static final String DEF_MARKER = "/*def*/";
    public static final String REF_MARKER = "/*ref*/";

    public void testLocalVariable() throws Exception { doTest(); }
    public void testLocalCaseVariable() throws Exception { doTest(); }
    public void testLocalCaseVariable2() throws Exception { doTest(); }
    public void testLocalCaseVariable3() throws Exception { doTest(); }
    public void testFunctionParameter() throws Exception { doTest(); }
    public void testFunctionResult() throws Exception { doTest(); }
    public void testMethodReceiver() throws Exception { doTest(); }
    public void testMethod() throws Exception { doTest(); }
    public void testFunction() throws Exception { doTest(); }

    private PsiElement def = null;
    private final Set<PsiElement> refSet = new HashSet<PsiElement>();

    @Override
    protected String getTestDataRelativePath() {
        return "usage/";
    }

    @Override
    protected void postProcessFilePsi(PsiFile psiFile, String fileContent) {
        detect(psiFile);
    }

    private void detect(PsiElement element) {
        PsiElement child = element.getFirstChild();
        while (child != null) {
            if (isNodeOfType(child, GoElementTypes.COMMENTS)) {
                if (DEF_MARKER.equals(child.getText())) {
                    assertNull(def);
                    def = nextLeaf(child, true);
                } else if (REF_MARKER.equals(child.getText())) {
                    refSet.add(nextLeaf(child, true));
                }
            } else {
                detect(child);
            }
            child = child.getNextSibling();
        }
    }

    @Override
    protected void assertTest() {
        assertNotNull(def);

        assertCorrectUsageOf(def);
        for (PsiElement element : refSet) {
            PsiReference reference = element.getContainingFile().findReferenceAt(element.getTextOffset());
            assertNotNull(reference);
            PsiElement resolve = reference.resolve();
            assertNotNull(resolve);
            assertEquals(def.getTextOffset(), resolve.getTextOffset());
        }
    }

    private void assertCorrectUsageOf(PsiElement element) {
        Map<Integer, PsiElement> expectedElements = getExpectedElements();
        for (PsiReference reference : findReferenceOf(element)) {
            PsiElement refElement = reference.getElement();
            int offset = refElement.getTextOffset();
            if (expectedElements.remove(offset) == null) {
                if (isReferenceToIdentifierOfSelector(expectedElements, refElement)) {
                    continue;
                }

                fail(getElementInfo(refElement) + " unexpected reference!");
            }
        }

        if (expectedElements.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder("Following references not found:\n");
        for (PsiElement psiElement : expectedElements.values()) {
            sb.append(getElementInfo(psiElement)).append("\n");
        }
        fail(sb.toString());
    }

    private boolean isReferenceToIdentifierOfSelector(Map<Integer, PsiElement> expectedElements,
                                                      PsiElement refElement) {
        if (refElement instanceof GoSelectorExpression) {
            GoLiteralIdentifier identifier = ((GoSelectorExpression) refElement).getIdentifier();
            if (identifier != null) {
                if (expectedElements.remove(identifier.getTextOffset()) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private Collection<PsiReference> findReferenceOf(PsiElement element) {
        while (element != null) {
            Collection<PsiReference> references = ReferencesSearch.search(element).findAll();
            if (!references.isEmpty()) {
                return references;
            }
            element = element.getParent();
        }
        return Collections.emptySet();
    }

    public Map<Integer,PsiElement> getExpectedElements() {
        Map<Integer, PsiElement> expectedElements = new HashMap<Integer, PsiElement>();
        for (PsiElement element : refSet) {
            expectedElements.put(element.getTextOffset(), element);
        }
        return expectedElements;
    }

    private String getElementInfo(PsiElement element) {
        Document doc = PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());
        int offset = element.getTextOffset();
        int line = doc.getLineNumber(offset);
        int col = offset - doc.getLineStartOffset(line);
        return String.format("[Element \"%s\" at (%d, %d)]", element.getText(), line + 1, col + 1);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        def = null;
        refSet.clear();
    }
}
