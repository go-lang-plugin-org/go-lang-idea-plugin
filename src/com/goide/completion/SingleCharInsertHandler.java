/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.completion;

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