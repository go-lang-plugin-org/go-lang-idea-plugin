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

import com.goide.psi.GoTopLevelDeclaration;
import com.intellij.codeInsight.hint.ImplementationTextSelectioner;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

public class GoImplementationTextSelectioner implements ImplementationTextSelectioner {
  @Override
  public int getTextStartOffset(@NotNull PsiElement o) {
    return getTextRange(o).getStartOffset();
  }

  @Override
  public int getTextEndOffset(@NotNull PsiElement o) {
    return getTextRange(o).getEndOffset();
  }

  @NotNull
  private static TextRange getTextRange(@NotNull PsiElement o) {
    return ObjectUtils.notNull(PsiTreeUtil.getParentOfType(o, GoTopLevelDeclaration.class), o).getTextRange();
  }
}
