package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;

public class PackageInsertHandler implements com.intellij.codeInsight.completion.InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {

        int offset = context.getTailOffset();

        if (context.getCompletionChar() == '.') {
            context.setAddCompletionChar(true);
        } else {
            if ( offset == context.getDocument().getTextLength() ||
                    ! context.getDocument().getText(new TextRange(offset, offset + 1)).equals("."))
                context.getDocument().insertString(offset, ".");
        }

        context.commitDocument();

        // if object is a function which has no parameters, move caret to the end of parenthesis.
        context.getEditor().getCaretModel().moveToOffset(offset + 1);
    }
}
