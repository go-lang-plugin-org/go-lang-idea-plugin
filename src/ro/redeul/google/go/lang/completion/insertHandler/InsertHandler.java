package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import static ro.redeul.google.go.util.EditorUtil.pressEnter;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

abstract class InsertHandler<T extends LookupElement>
    implements com.intellij.codeInsight.completion.InsertHandler<T> {

    @Override
    public void handleInsert(InsertionContext context, T item) {
        int offset = context.getTailOffset();
        String text = getInsertionText();
        context.getDocument().insertString(offset, text);

        if (nextCaretPosition() != 0)
            context.getEditor().getCaretModel().moveToOffset(
                offset + nextCaretPosition());

        if (shouldReformat())
            reformatPositions(context.getFile(), offset,
                              offset + text.length());

        if (shouldPressEnter())
            pressEnter(context.getEditor());
    }

    protected abstract String getInsertionText();

    protected abstract boolean shouldPressEnter();

    int nextCaretPosition() {
        return 1;
    }

    boolean shouldReformat() {
        return false;
    }
}
