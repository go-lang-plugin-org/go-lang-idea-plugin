package ro.redeul.google.go.highlight;

import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class HighlightExitPointsHandlerFactory implements HighlightUsagesHandlerFactory {
    @Override
    public HighlightUsagesHandlerBase createHighlightUsagesHandler(Editor editor, PsiFile file) {
        int maxOffset = editor.getDocument().getTextLength() - 1;
        int offset = Math.min(editor.getCaretModel().getOffset(), maxOffset);
        PsiElement target = file.findElementAt(offset);
        return HighlightExitPointsHandler.createForElement(editor, file, target);
    }
}
