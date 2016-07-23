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
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.quickfix.GoDeleteQuickFix;
import com.intellij.codeInspection.CleanupLocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoRedundantSecondIndexInSlicesInspection extends GoInspectionBase implements CleanupLocalInspectionTool {
  public final static String DELETE_REDUNDANT_INDEX_QUICK_FIX_NAME = "Delete redundant index";
  private static final GoDeleteQuickFix DELETE_REDUNDANT_INDEX_QUICK_FIX =
    new GoDeleteQuickFix(DELETE_REDUNDANT_INDEX_QUICK_FIX_NAME, GoCallExpr.class);

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitIndexOrSliceExpr(@NotNull GoIndexOrSliceExpr o) {
        GoExpression expression = o.getExpression();
        PsiReference reference = expression != null ? expression.getReference() : null;
        PsiElement resolvedArrayReference = reference != null ? reference.resolve() : null;
        if (reference == null ||
            resolvedArrayReference == null ||
            !(expression instanceof GoReferenceExpression) ||
            ((GoReferenceExpression)expression).getQualifier() != null) {
          return;
        }
        Trinity<GoExpression, GoExpression, GoExpression> indexes = o.getIndices();
        if (indexes.third == null && indexes.second instanceof GoCallExpr) {
          GoCallExpr call = (GoCallExpr)indexes.second;
          if (isLenFunctionCall(call)) {
            GoExpression argument = ContainerUtil.getFirstItem(call.getArgumentList().getExpressionList());
            if (argument != null && argument.getReference() != null && resolvedArrayReference.equals(argument.getReference().resolve())) {
              holder.registerProblem(call, "Redundant index", ProblemHighlightType.LIKE_UNUSED_SYMBOL, DELETE_REDUNDANT_INDEX_QUICK_FIX);
            }
          }
        }
      }
    };
  }

  private static boolean isLenFunctionCall(@NotNull GoCallExpr callExpr) {
    GoExpression functionReferenceExpression = callExpr.getExpression();
    if (!(functionReferenceExpression instanceof GoReferenceExpression) || !functionReferenceExpression.textMatches("len")) {
      return false;
    }
    List<GoExpression> callArguments = callExpr.getArgumentList().getExpressionList();
    if (callArguments.size() != 1) {
      return false;
    }
    return GoPsiImplUtil.builtin(((GoReferenceExpression)functionReferenceExpression).resolve());
  }
}
