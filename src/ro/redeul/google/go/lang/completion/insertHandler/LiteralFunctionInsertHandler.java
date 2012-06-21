package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;

public class LiteralFunctionInsertHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        int offset = context.getTailOffset();
        context.getDocument().insertString(offset, "(){\n    \n}()\n");
        context.getEditor().getCaretModel().moveToOffset(offset + 8);
    }
}
