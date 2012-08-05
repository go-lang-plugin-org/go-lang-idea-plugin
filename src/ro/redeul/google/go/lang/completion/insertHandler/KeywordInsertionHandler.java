package ro.redeul.google.go.lang.completion.insertHandler;

import com.intellij.codeInsight.lookup.LookupElement;

public class KeywordInsertionHandler extends InsertHandler<LookupElement>{

    @Override
    protected String getInsertionText() {
        return " ";
    }

    @Override
    protected boolean shouldPressEnter() {
        return false;
    }

    @Override
    protected int nextCaretPosition() {
        return 1;
    }
}

