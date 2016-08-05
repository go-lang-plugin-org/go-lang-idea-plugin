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

package com.goide.usages;

import com.goide.psi.*;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoReadWriteAccessDetector extends ReadWriteAccessDetector {
  @Override
  public boolean isReadWriteAccessible(@Nullable PsiElement e) {
    return e instanceof GoVarDefinition ||
           e instanceof GoConstDefinition ||
           e instanceof GoParamDefinition ||
           e instanceof GoReceiver ||
           e instanceof GoFieldDefinition;
  }

  @Override
  public boolean isDeclarationWriteAccess(@Nullable PsiElement e) {
    return e instanceof GoVarDefinition || e instanceof GoConstDefinition;
  }

  @NotNull
  @Override
  public Access getReferenceAccess(@Nullable PsiElement referencedElement, @NotNull PsiReference reference) {
    return getExpressionAccess(reference.getElement());
  }

  @NotNull
  @Override
  public Access getExpressionAccess(@Nullable PsiElement e) {
    if (e instanceof GoFieldName) {
      return e.getParent() instanceof GoKey ? Access.Write : Access.Read;
    }
    GoReferenceExpression referenceExpression = PsiTreeUtil.getNonStrictParentOfType(e, GoReferenceExpression.class);
    return referenceExpression != null ? referenceExpression.getReadWriteAccess() : Access.Read;
  }
}
