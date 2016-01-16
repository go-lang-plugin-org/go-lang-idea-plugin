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

public class GoReadWriteAccessDetector extends ReadWriteAccessDetector {
  @Override
  public boolean isReadWriteAccessible(final PsiElement element) {
    return element instanceof GoVarDefinition ||
           element instanceof GoConstDefinition ||
           element instanceof GoParamDefinition ||
           element instanceof GoReceiver ||
           element instanceof GoFunctionOrMethodDeclaration;
  }

  @Override
  public boolean isDeclarationWriteAccess(PsiElement element) {
    return element instanceof GoVarDefinition ||
           element instanceof GoConstDefinition;
  }

  @Override
  public Access getReferenceAccess(PsiElement referencedElement, PsiReference reference) {
     return getExpressionAccess(reference.getElement());
  }

  @Override
  public Access getExpressionAccess(PsiElement expression) {
    GoCompositeElement parent =
      PsiTreeUtil.getParentOfType(expression, GoAssignmentStatement.class, GoShortVarDeclaration.class, GoArgumentList.class, GoIndexOrSliceExpr.class, GoBlock.class);

    if (parent instanceof GoArgumentList ||
        parent instanceof GoIndexOrSliceExpr ||
        parent instanceof GoBlock) {
      return Access.Read;
    }
    return PsiTreeUtil.getParentOfType(expression, GoLeftHandExprList.class) == null ? Access.Read : Access.Write;
  }
}
