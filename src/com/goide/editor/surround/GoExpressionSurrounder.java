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

package com.goide.editor.surround;

import com.goide.psi.GoExpression;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoExpressionSurrounder implements Surrounder {
  @Override
  public boolean isApplicable(@NotNull PsiElement[] elements) {
    return getExpression(elements) != null;
  }

  @Nullable
  protected TextRange surroundWithParenthesis(@NotNull PsiElement[] elements, boolean withNot) {
    GoExpression expression = getExpression(elements);
    if (expression == null) return null;

    String text = (withNot ? "!" : "") + "(" + expression.getText() + ")";
    GoExpression parenthExprNode = GoElementFactory.createExpression(expression.getProject(), text);
    PsiElement replace = expression.replace(parenthExprNode);
    int endOffset = replace.getTextRange().getEndOffset();
    return TextRange.create(endOffset, endOffset);
  }

  @Nullable
  protected GoExpression getExpression(@NotNull PsiElement[] elements) {
    return ObjectUtils.tryCast(ArrayUtil.getFirstElement(elements), GoExpression.class);
  }
}
