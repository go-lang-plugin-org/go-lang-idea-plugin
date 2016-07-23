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
import com.goide.quickfix.GoDeleteRangeQuickFix;
import com.intellij.codeInspection.CleanupLocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoRedundantBlankArgInRangeInspection extends GoInspectionBase implements CleanupLocalInspectionTool {
  public final static String DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME = "Delete blank argument";

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitRangeClause(@NotNull GoRangeClause o) {
        List<GoExpression> leftExpressions = o.getLeftExpressionsList();
        PsiElement range = o.getRange();
        if (range == null) return;
        if (leftExpressions.size() == 2 && isBlankGoReferenceExpression(leftExpressions.get(1))) {
          if (isBlankGoReferenceExpression(leftExpressions.get(0))) {
            registerBlankArgumentProblem(holder, leftExpressions.get(0), range.getPrevSibling());
          }
          else if (leftExpressions.get(0).getNextSibling() != null) {
            registerBlankArgumentProblem(holder, leftExpressions.get(0).getNextSibling(), leftExpressions.get(1));
          }
        }
        else if (leftExpressions.size() == 1 && isBlankGoReferenceExpression(leftExpressions.get(0))) {
          registerBlankArgumentProblem(holder, leftExpressions.get(0), range.getPrevSibling());
        }

        List<GoVarDefinition> leftDefinitions = o.getVarDefinitionList();
        if (leftDefinitions.size() == 2 && isBlankGoVarDefinition(leftDefinitions.get(1))) {
          registerBlankArgumentProblem(holder, leftDefinitions.get(0).getNextSibling(), leftDefinitions.get(1));
        }
      }
    };
  }

  private static void registerBlankArgumentProblem(@NotNull ProblemsHolder holder,
                                                   @NotNull PsiElement start,
                                                   @NotNull PsiElement end) {
    GoDeleteRangeQuickFix fix = new GoDeleteRangeQuickFix(start, end, DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME);
    holder.registerProblem(holder.getManager().createProblemDescriptor(start, end, "Redundant <code>_</code> expression",
                                                                       ProblemHighlightType.LIKE_UNUSED_SYMBOL, holder.isOnTheFly(), fix));
  }

  private static boolean isBlankGoVarDefinition(@Nullable PsiElement o) {
    return o instanceof GoVarDefinition && o.textMatches("_");
  }

  private static boolean isBlankGoReferenceExpression(@Nullable PsiElement o) {
    return o instanceof GoReferenceExpression && o.textMatches("_");
  }
}
