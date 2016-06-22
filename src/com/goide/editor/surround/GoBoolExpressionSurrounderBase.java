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
import com.goide.psi.GoIfStatement;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoBoolExpressionSurrounderBase extends GoExpressionSurrounder {
  @Override
  public boolean isApplicable(@NotNull PsiElement[] elements) {
    GoExpression expression = getExpression(elements);
    return expression != null && GoTypeUtil.isBoolean(expression.getGoType(null));
  }

  @Nullable
  protected TextRange surroundExpressionWithIfElse(@NotNull PsiElement[] elements, boolean withElse) {
    GoExpression expression = getExpression(elements);
    if (expression == null) return null;
    String condition = expression.getText();
    GoIfStatement ifStatement = GoElementFactory.createIfStatement(expression.getProject(), condition, "", withElse ? "" : null);
    PsiElement replace = expression.replace(ifStatement);
    int offset = replace.getTextRange().getEndOffset();
    return TextRange.create(offset, offset);
  }
}
