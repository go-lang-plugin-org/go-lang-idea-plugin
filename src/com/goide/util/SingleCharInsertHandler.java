package com.goide.util;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.BasicInsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public class SingleCharInsertHandler extends BasicInsertHandler<LookupElement> {
  private final char myChar;

  public SingleCharInsertHandler(char aChar) {
    myChar = aChar;
  }

  @Override
  public void handleInsert(@NotNull InsertionContext context, LookupElement item) {
    Editor editor = context.getEditor();
    int tailOffset = context.getTailOffset();
    Document document = editor.getDocument();
    context.commitDocument();
    boolean staysAtChar = document.getTextLength() > tailOffset &&
                          document.getCharsSequence().charAt(tailOffset) == myChar;

    context.setAddCompletionChar(false);
    if (!staysAtChar) {
      document.insertString(tailOffset, String.valueOf(myChar));
    }
    editor.getCaretModel().moveToOffset(tailOffset + 1);

    AutoPopupController.getInstance(context.getProject()).scheduleAutoPopup(editor);
  }
}