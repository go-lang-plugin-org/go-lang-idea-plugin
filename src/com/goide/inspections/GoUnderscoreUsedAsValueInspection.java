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
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GoUnderscoreUsedAsValueInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitReferenceExpression(@NotNull GoReferenceExpression o) {
        super.visitReferenceExpression(o);
        if (!o.getIdentifier().textMatches("_")) return;
        PsiElement parent = o.getParent();
        if (parent instanceof GoSendStatement) return;
        if (parent instanceof GoRangeClause) {
          if (!((GoRangeClause)parent).getExpressionList().contains(o)) return;
        }
        if (PsiTreeUtil.getParentOfType(o, GoLeftHandExprList.class, GoArgumentList.class) instanceof GoLeftHandExprList) return;
        holder.registerProblem(o, "Cannot use '_' as value", ProblemHighlightType.ERROR);
      }
    };
  }
}
