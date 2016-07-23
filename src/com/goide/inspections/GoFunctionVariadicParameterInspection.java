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

package com.goide.inspections;

import com.goide.psi.*;
import com.goide.quickfix.GoDeleteQuickFix;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.goide.GoTypes.TRIPLE_DOT;

public class GoFunctionVariadicParameterInspection extends GoInspectionBase {
  private static final GoDeleteQuickFix DELETE_QUICK_FIX = new GoDeleteQuickFix("Delete ...", TRIPLE_DOT);

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitCompositeElement(@NotNull GoCompositeElement o) {
        if (o instanceof GoSignatureOwner) {
          GoSignature signature = ((GoSignatureOwner)o).getSignature();
          if (signature != null) {
            checkResult(signature, holder);
            checkParameters(signature, holder);
          }
        }
      }
    };
  }

  private static void checkResult(@NotNull GoSignature o, @NotNull ProblemsHolder holder) {
    GoResult result = o.getResult();
    if (result == null) return;
    GoParameters parameters = result.getParameters();
    if (parameters == null) return;
    for (GoParameterDeclaration declaration : parameters.getParameterDeclarationList()) {
      PsiElement dot = declaration.getTripleDot();
      if (dot != null) {
        holder.registerProblem(dot, "Cannot use <code>...</code> in output argument list", ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                               DELETE_QUICK_FIX);
      }
    }
  }

  private static void checkParameters(@NotNull GoSignature o, @NotNull ProblemsHolder holder) {
    GoParameters parameters = o.getParameters();
    List<GoParameterDeclaration> list = parameters.getParameterDeclarationList();
    int size = list.size();
    for (int i = 0; i < size; i++) {
      GoParameterDeclaration declaration = list.get(i);
      PsiElement dot = declaration.getTripleDot();
      if (dot != null) {
        if (declaration.getParamDefinitionList().size() > 1 || i != size - 1) {
          holder.registerProblem(dot, "Can only use <code>...</code> as final argument in list", ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                 DELETE_QUICK_FIX);
        }
      }
    }
  }
}
