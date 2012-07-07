package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

public class FunctionInsertHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        int offset = context.getTailOffset();
        if ( ! context.getDocument().getText(new TextRange(offset, offset + 1)).equals("("))
           context.getDocument().insertString(offset, "()");

        // if object is a function which has no parameters, move caret to the end of parenthesis.
        if (isFunctionWithoutParameters(item.getObject())) {
            context.getEditor().getCaretModel().moveToOffset(offset + 2);
        } else {
            // otherwise, put caret to the middle of parenthesis.
            context.getEditor().getCaretModel().moveToOffset(offset + 1);
        }
    }

    private static boolean isFunctionWithoutParameters(Object object) {
        if (object instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration declaration = (GoFunctionDeclaration) object;
            if (declaration.getParameters().length == 0) {
                return true;
            }
        }
        return false;
    }
}
