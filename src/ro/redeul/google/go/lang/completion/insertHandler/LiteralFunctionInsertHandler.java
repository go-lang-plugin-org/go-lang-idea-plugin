package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

import static ro.redeul.google.go.util.EditorUtil.pressEnterAtLineEnd;
import static ro.redeul.google.go.util.EditorUtil.reformatLines;

public class LiteralFunctionInsertHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        int offset = context.getTailOffset();
        Document doc = context.getDocument();
        doc.insertString(offset, "(){\n}()");
        Editor editor = context.getEditor();

        int line = doc.getLineNumber(offset);
        reformatLines(context.getFile(), editor, line, line + 1);
        pressEnterAtLineEnd(editor);
    }
}
