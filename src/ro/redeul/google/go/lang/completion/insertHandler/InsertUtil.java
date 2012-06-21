package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertionContext;

class InsertUtil {
    public static void insertParenthesis(InsertionContext context) {
        int offset = context.getTailOffset();
        context.getDocument().insertString(offset, "(\n    \n)\n");
        context.getEditor().getCaretModel().moveToOffset(offset + 6);
    }
}
