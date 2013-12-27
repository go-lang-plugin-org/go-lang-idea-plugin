package ro.redeul.google.go.lang.completion.smartEnter;

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.ForFixer;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.FunctionFixer;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.IfFixer;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.SmartEnterFixer;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

public class GoSmartEnterProcessor extends SmartEnterProcessor {

    private static final SmartEnterFixer[] FIXERS = new SmartEnterFixer[]{
            new FunctionFixer(),
            new IfFixer(),
            new ForFixer(),
    };

    @Override
    public boolean process(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        Document document = editor.getDocument();

        int line = document.getLineNumber(offset);
        PsiElement elementToFix = psiFile.findElementAt(offset);

        // When the caret is put at the end of document, no element could be found, find the previous one.
        if (elementToFix == null) {
            elementToFix = psiFile.findElementAt(offset - 1);
        }

        if (isWhiteSpaceNode(elementToFix)) {
            PsiElement previous = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(elementToFix);

            if (previous != null) {
                if ( document.getLineStartOffset(line) > previous.getTextOffset() + previous.getTextLength() ) {
                    PsiElement next = GoPsiUtils.getNextSiblingIfItsWhiteSpaceOrComment(elementToFix);
                    if (next != null) {
                        previous = next;
                    }
                }

                elementToFix = psiFile.findElementAt(previous.getTextRange().getEndOffset() - 1);
            } else if (elementToFix != null && elementToFix.getParent() != null) {
                elementToFix = elementToFix.getParent();
            }
        }

        for (SmartEnterFixer fixer : FIXERS) {
            if (fixer.process(editor, elementToFix)) {
                return true;
            }
        }
        return false;
    }
}
