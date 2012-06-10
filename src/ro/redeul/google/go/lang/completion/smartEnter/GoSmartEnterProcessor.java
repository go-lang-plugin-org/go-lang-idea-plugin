package ro.redeul.google.go.lang.completion.smartEnter;

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.FunctionFixer;
import ro.redeul.google.go.lang.completion.smartEnter.fixers.SmartEnterFixer;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isWhiteSpaceNode;

public class GoSmartEnterProcessor extends SmartEnterProcessor {

    private static final SmartEnterFixer[] FIXERS = new SmartEnterFixer[]{
            new FunctionFixer()
    };

    @Override
    public boolean process(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        PsiElement atCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        if (isWhiteSpaceNode(atCaret)) {
            PsiElement previous = GoPsiUtils.getPrevSiblingIfItsWhiteSpaceOrComment(atCaret);
            if (previous != null) {
                atCaret = previous;
            } else if (atCaret.getParent() != null) {
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
