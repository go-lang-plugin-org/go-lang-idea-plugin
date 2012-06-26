package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertionContext;

import static ro.redeul.google.go.util.EditorUtil.pressEnterAtLineEnd;

class InsertUtil {
    public static void insertParenthesis(InsertionContext context) {
        int offset = context.getTailOffset();
        context.getDocument().insertString(offset, "(\n)\n");
        pressEnterAtLineEnd(context.getEditor());
    }

    public static void insertCurlyBraces(InsertionContext context) {
        int offset = context.getTailOffset();
        context.getDocument().insertString(offset, "{\n}\n");
        pressEnterAtLineEnd(context.getEditor());
    }
}
