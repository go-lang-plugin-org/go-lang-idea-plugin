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

package com.goide.quickfix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoDeleteRangeQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
  private final String myName;

  public GoDeleteRangeQuickFix(@Nullable PsiElement startElement,
                               @Nullable PsiElement endElement,
                               String name) {
    super(startElement, endElement);
    myName = name;
  }

  @NotNull
  @Override
  public String getText() {
    return myName;
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return "Delete elements";
  }

  //delete range include start,end
  @Override
  public void invoke(@NotNull Project project,
                     @NotNull PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement start,
                     @NotNull PsiElement end) {
    if (start.isValid() && end.isValid()) {
      PsiElement parent = PsiTreeUtil.findCommonParent(start, end);
      if (parent != null) {
        parent.getNode().removeRange(start.getNode(), end.getNode().getTreeNext());
      }
    }
  }
}