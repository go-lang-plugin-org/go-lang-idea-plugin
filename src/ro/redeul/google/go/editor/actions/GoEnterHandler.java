package ro.redeul.google.go.editor.actions;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 27, 2010
 * Time: 6:47:04 PM
 */
public class GoEnterHandler implements EnterHandlerDelegate {

    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        return Result.Continue;
    }

    public Result preprocessEnter(PsiFile file, Editor editor,
                                  Ref<Integer> caretOffset,
                                  Ref<Integer> caretAdvance,
                                  DataContext dataContext, EditorActionHandler originalHandler) {

        String text = editor.getDocument().getText();

        if (StringUtil.isEmpty(text)) {
          return Result.Continue;
        }

        final int caret = editor.getCaretModel().getOffset();
        final EditorHighlighter highlighter = ((EditorEx)editor).getHighlighter();

        if (caret >= 1 && caret < text.length() && CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER) {
            HighlighterIterator iterator = highlighter.createIterator(caret);
            iterator.retreat();
            while (!iterator.atEnd() && GoTokenTypes.wsNLS == iterator.getTokenType() && GoTokenTypes.wsWS == iterator.getTokenType() ) {
              iterator.retreat();
            }

            if (iterator.getTokenType() == GoTokenTypes.pLCURCLY ) {
                PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());
                CodeStyleManager.getInstance(file.getProject()).adjustLineIndent(file, editor.getCaretModel().getOffset());
                return Result.DefaultForceIndent;
            }
        }

        return Result.Continue;
    }

}
