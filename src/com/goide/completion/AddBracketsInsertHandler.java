/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddBracketsInsertHandler extends ParenthesesInsertHandler<LookupElement> {
  @Override
  protected boolean placeCaretInsideParentheses(InsertionContext context, LookupElement element) {
    return true;
  }

  @Override
  public void handleInsert(@NotNull InsertionContext context, LookupElement item) {
    Editor editor = context.getEditor();
    Document document = editor.getDocument();
    context.commitDocument();
    PsiElement element = findNextToken(context);
    char completionChar = context.getCompletionChar();
    boolean putCaretInside = completionChar == '[' || placeCaretInsideParentheses(context, item);
    if (completionChar == '[') {
      context.setAddCompletionChar(false);
    }

    int tail;
    if (isToken(element, "[")) {
      tail = element.getTextRange().getStartOffset();
      if (completionChar != '[' && completionChar != '\t') {
        editor.getCaretModel().moveToOffset(context.getTailOffset());
      }
      else {
        editor.getCaretModel().moveToOffset(tail + 1);
      }

      context.setTailOffset(tail + 1);
      PsiElement list = element.getParent();
      PsiElement last = list.getLastChild();
      if (isToken(last, "]")) {
        int rparenthOffset = last.getTextRange().getStartOffset();
        context.setTailOffset(rparenthOffset + 1);
        if (!putCaretInside) {
          for (int i = tail + 1; i < rparenthOffset; ++i) {
            if (!Character.isWhitespace(document.getCharsSequence().charAt(i))) {
              return;
            }
          }

          editor.getCaretModel().moveToOffset(context.getTailOffset());
        }
        else {
          editor.getCaretModel().moveToOffset(tail + 1);
        }

        return;
      }
    }
    else {
      document.insertString(context.getTailOffset(), "[");
      editor.getCaretModel().moveToOffset(context.getTailOffset());
    }


    if (context.getCompletionChar() == '[') {
      tail = context.getTailOffset();
      if (tail < document.getTextLength() && StringUtil.isJavaIdentifierPart(document.getCharsSequence().charAt(tail))) {
        return;
      }
    }

    document.insertString(context.getTailOffset(), "]");
    if (!putCaretInside) {
      editor.getCaretModel().moveToOffset(context.getTailOffset());
    }
  }

  private static boolean isToken(@Nullable PsiElement element, @NotNull String text) {
    return element != null && element.textMatches(text);
  }
}
