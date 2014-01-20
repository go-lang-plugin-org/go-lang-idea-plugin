package com.goide.util;

import com.intellij.codeInsight.completion.BasicInsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

public class SingleCharInsertHandler extends BasicInsertHandler<LookupElement> {
  private char myChar;

  public SingleCharInsertHandler(char aChar) {
    myChar = aChar;
  }

  @Override
  public void handleInsert(InsertionContext context, LookupElement item) {
    if (context.getCompletionChar() != myChar) {
      final Editor editor = context.getEditor();
      final Document document = editor.getDocument();
      context.commitDocument();
      int tailOffset = context.getTailOffset();
      document.insertString(tailOffset, String.valueOf(myChar));
      editor.getCaretModel().moveToOffset(tailOffset + 1);
    }
  }
}