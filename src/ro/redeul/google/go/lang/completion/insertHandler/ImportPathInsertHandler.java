package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;

public class ImportPathInsertHandler implements com.intellij.codeInsight.completion.InsertHandler<LookupElement> {
    public static ImportPathInsertHandler INSTANCE = new ImportPathInsertHandler();

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        context.setAddCompletionChar(false);

        int startOffset = context.getStartOffset();
        int tailOffset = context.getTailOffset();

        if (startOffset <= 0 )
            return;

        String start = context.getDocument().getText(TextRange.create(startOffset - 1, startOffset));
        if ( ! context.getDocument().getText(new TextRange(tailOffset, tailOffset + 1)).equals(start))
            context.getDocument().insertString(tailOffset, start);

        context.commitDocument();

        // if object is a function which has no parameters, move caret to the end of parenthesis.
        context.getEditor().getCaretModel().moveToOffset(tailOffset + 1);
    }
}
