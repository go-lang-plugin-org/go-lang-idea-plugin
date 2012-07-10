package ro.redeul.google.go.lang.completion.smartEnter;

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.FunctionFixer;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.IfFixer;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.SmartEnterFixer;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

public class GoSmartEnterProcessor extends SmartEnterProcessor {

    private static final SmartEnterFixer[] FIXERS = new SmartEnterFixer[]{
            new FunctionFixer(),
            new IfFixer(),
    };

    @Override
    public boolean process(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement atCaret = psiFile.findElementAt(offset);

        // When the caret is put at the end of document, no element could be found, find the previous one.
        if (atCaret == null) {
            atCaret = psiFile.findElementAt(offset - 1);
        }

        if (isWhiteSpaceNode(atCaret)) {
            PsiElement previous = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(atCaret);
            if (previous != null) {
                atCaret = previous;
            } else if (atCaret != null && atCaret.getParent() != null) {
                atCaret = atCaret.getParent();
            }
        }

        for (SmartEnterFixer fixer : FIXERS) {
            if (fixer.process(editor, atCaret)) {
                return true;
            }
        }
        return false;
    }
}
