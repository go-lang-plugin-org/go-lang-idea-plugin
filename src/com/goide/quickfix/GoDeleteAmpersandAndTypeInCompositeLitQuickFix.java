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

import com.goide.inspections.GoRedundantTypeDeclInCompositeLit;
import com.goide.psi.*;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class GoDeleteAmpersandAndTypeInCompositeLitQuickFix extends LocalQuickFixBase {

  public GoDeleteAmpersandAndTypeInCompositeLitQuickFix() {
    super(GoRedundantTypeDeclInCompositeLit.DELETE_TYPE_DECLARATION_QUICK_FIX_NAME);
  }

  @Override
  public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
    PsiElement element = descriptor.getPsiElement();
    if (element != null && element.isValid()) {
      if (element instanceof GoUnaryExpr) {
        GoUnaryExpr unaryExpr = (GoUnaryExpr)element;
        GoExpression expr = unaryExpr.getExpression();
        if (unaryExpr.getBitAnd() != null && expr instanceof GoCompositeLit) {
          GoLiteralValue literalValue = ((GoCompositeLit)expr).getLiteralValue();
          if (literalValue != null) {
            unaryExpr.replace(literalValue);
          }
        }
      }
      else if (element instanceof GoTypeReferenceExpression) {
        PsiElement parent = element.getParent();
        if (parent instanceof GoCompositeLit) {
          GoLiteralValue literalValue = ((GoCompositeLit)parent).getLiteralValue();
          if (literalValue != null) {
            parent.replace(literalValue);
          }
        }
      }
    }
  }
}
