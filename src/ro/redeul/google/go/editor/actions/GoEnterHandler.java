package ro.redeul.google.go.editor.actions;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 27, 2010
 * Time: 6:47:04 PM
 */
public class GoEnterHandler extends EnterHandlerDelegateAdapter {

    @Override
    public Result preprocessEnter(@NotNull PsiFile file, @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset,
                                  @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext,
                                  EditorActionHandler originalHandler) {
        Integer offset = caretOffset.get();
        if (offset != null && file instanceof GoFile) {
            Document document = editor.getDocument();
            int start = document.getLineStartOffset(document.getLineNumber(offset));
            // If previous line ends with ':', adjust its indent.
            if (document.getText(new TextRange(start, offset)).trim().endsWith(":")) {
                CodeStyleManager.getInstance(file.getProject()).adjustLineIndent(file, start);
                caretOffset.set(editor.getCaretModel().getOffset());
            }
        }

        return Result.Continue;
    }
}
