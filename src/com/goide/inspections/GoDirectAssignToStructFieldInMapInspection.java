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
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoDirectAssignToStructFieldInMapInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitLeftHandExprList(@NotNull GoLeftHandExprList o) {
        super.visitLeftHandExprList(o);
        for (GoExpression expression : o.getExpressionList()) {
          if (!(expression instanceof GoSelectorExpr)) continue;
          GoExpression expr = ((GoSelectorExpr)expression).getExpressionList().get(0);
          if (!(expr instanceof GoIndexOrSliceExpr)) continue;
          GoType exprType = expr.getGoType(null);
          if (exprType == null || !(exprType.getParent() instanceof GoMapType)) continue;
          holder.registerProblem(o, "cannot assign to " + expression.getText(), GENERIC_ERROR_OR_WARNING);
        }
      }
    };
  }
}
