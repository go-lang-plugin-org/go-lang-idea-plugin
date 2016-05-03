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

package com.goide.editor;

import com.goide.psi.*;
import com.intellij.codeInsight.editorActions.wordSelection.AbstractWordSelectioner;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoWordSelectioner extends AbstractWordSelectioner {
  @Override
  public boolean canSelect(@NotNull PsiElement e) {
    return e.getContainingFile() instanceof GoFile;
  }

  @Override
  public List<TextRange> select(@NotNull PsiElement e, CharSequence editorText, int cursorOffset, Editor editor) {
    PsiElement parent = e.getParent();
    List<TextRange> result = super.select(e, editorText, cursorOffset, editor);
    if (parent instanceof GoImportString || parent instanceof GoStringLiteral) {
      result.add(ElementManipulators.getValueTextRange(parent).shiftRight(parent.getTextRange().getStartOffset()));
    }
    else if (parent instanceof GoImportDeclaration) {
      result.addAll(extend(editorText, ((GoImportDeclaration)parent).getImportSpecList(), false));
    }
    else if (e instanceof GoSimpleStatement) {
      result.addAll(expandToWholeLine(editorText, e.getTextRange()));
    }
    else if (e instanceof GoArgumentList || e instanceof GoParameters) {
      if (e.getTextLength() > 2) {
        result.add(TextRange.create(e.getTextRange().getStartOffset() + 1, e.getTextRange().getEndOffset() - 1));
      }
    }
    else if (e instanceof GoBlock) {
      result.addAll(extend(editorText, ((GoBlock)e).getStatementList(), true));
    }
    else if (parent instanceof GoNamedSignatureOwner) {
      GoSignature signature = ((GoNamedSignatureOwner)parent).getSignature();
      if (signature != null) {
        int nameStartOffset = parent.getTextOffset();
        result.add(TextRange.create(nameStartOffset, signature.getParameters().getTextRange().getEndOffset()));
        result.add(TextRange.create(nameStartOffset, signature.getTextRange().getEndOffset()));
      }
    }
    return result;
  }

  @NotNull
  private static List<TextRange> extend(@NotNull CharSequence editorText, @NotNull List<? extends PsiElement> list, boolean expand) {
    PsiElement first = ContainerUtil.getFirstItem(list);
    PsiElement last = ContainerUtil.getLastItem(list);
    if (first != null && last != null) {
      TextRange range = TextRange.create(first.getTextRange().getStartOffset(), last.getTextRange().getEndOffset());
      if (!expand) return ContainerUtil.newSmartList(range);
      return expandToWholeLine(editorText, range);
    }
    return ContainerUtil.emptyList();
  }
}
