package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;

import static ro.redeul.google.go.lang.completion.insertHandler.InsertUtil.insertCurlyBraces;

public class CurlyBracesInsertHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        insertCurlyBraces(context);
    }
}