package ro.redeul.google.go.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import ro.redeul.google.go.GoFileBasedPsiTestCase;

public abstract class GoPsiResolveTestCase extends GoFileBasedPsiTestCase {

    public String REF_MARKER = "/*ref*/";
    public String NON_REF_MARKER = "/*no ref*/";
    public String DEF_MARKER = "/*def*/";

    PsiReference ref;
    PsiElement def;
    int noRefPosition;
    PsiFile noRefFile;

    @Override
    protected String getTestDataRelativePath() {
        return "psi/resolve/";
    }

    @Override
    protected void postProcessFilePsi(PsiFile psiFile, String fileContent) {
        getDefinition(psiFile, fileContent);
        getReference(psiFile, fileContent);
        getNoReferencePosition(psiFile, fileContent);
    }

    protected void assertTest() {
        if (noRefPosition != -1 ) {
            assertIfNoReference();
        } else {
            assertNotNull("Source position is not at a reference", ref);

            PsiElement resolvedDefinition = ref.resolve();
            if (def != null) {
                assertNotNull("The resolving should have been been a success",
                              resolvedDefinition);
                while (resolvedDefinition.getStartOffsetInParent() == 0) {
                    resolvedDefinition = resolvedDefinition.getParent();
                }

                assertSame(def.getNavigationElement(), resolvedDefinition.getNavigationElement());
            } else {
                assertNull("The resolving should have failed", resolvedDefinition);
            }
        }
    }

    private void assertIfNoReference() {
        assertNull("This should not resolve as reference", noRefFile.findReferenceAt(noRefPosition));
    }

    private void getDefinition(PsiFile psiFile, String fileContent) {
        if (def != null) {
            return;
        }

        int position = fileContent.indexOf(DEF_MARKER);
        if (position > 0) {
            def = psiFile.findElementAt(position + DEF_MARKER.length());
            while (def != null && def.getStartOffsetInParent() == 0) {
                def = def.getParent();
            }
        }
    }

    private void getReference(PsiFile psiFile, String fileContent) {
        if (ref != null) {
            return;
        }

        int position = fileContent.indexOf(REF_MARKER);
        if (position > 0) {
            ref = psiFile.findReferenceAt(position + REF_MARKER.length());
        }
    }

    private void getNoReferencePosition(PsiFile psiFile, String fileContent) {
        noRefPosition = fileContent.indexOf(NON_REF_MARKER);
        if (noRefPosition != -1) {
            noRefPosition += NON_REF_MARKER.length();
            noRefFile = psiFile;
        }
    }
}
