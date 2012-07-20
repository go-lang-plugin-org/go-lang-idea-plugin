package ro.redeul.google.go.codeInsight.editorActions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.util.EditorUtil.reformatLines;

public class ColonTypedHandler extends TypedHandlerDelegate {
    @Override
    public Result charTyped(char c, Project project, Editor editor, @NotNull PsiFile file) {
        if (c == ':' && file instanceof GoFile) {
            colonTyped(editor, file);
        }
        return super.charTyped(c, project, editor, file);
    }

    // If colon is typed in non string element, reformat current line.
    private void colonTyped(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element != null && findParentOfType(element, GoLiteralString.class) == null) {
            int line = editor.getDocument().getLineNumber(element.getTextOffset());
            reformatLines(file, editor, line, line);
        }
    }
}
