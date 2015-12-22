/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;

public class GoRangeIterationOnIllegalTypeInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitRangeClause(@NotNull GoRangeClause o) {
        super.visitRangeClause(o);

        if (o.getExpressionList().size() != 1) return;

        GoExpression expression = o.getRangeExpression();
        if (expression == null || expression instanceof GoStringLiteral) return;

        GoType goType = expression.getGoType(null);
        if (goType == null) return;

        if (isValidType(goType) || isValidTypeRefExpression(goType)) return;

        holder.registerProblem(expression, "Cannot range over data", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
      }
    };
  }

  private static boolean isValidType(GoType goType) {
    if (goType instanceof GoSpecType) {
      goType = ((GoSpecType)goType).getType();
    }

    return goType instanceof GoArrayOrSliceType ||
           goType instanceof GoMapType ||
           goType instanceof GoChannelType ||
           (goType.textMatches("string") && GoPsiImplUtil.builtin(goType));
  }

  private static boolean isValidTypeRefExpression(GoType goType) {
    if (goType instanceof GoParType) {
      goType = ((GoParType)goType).getType();
    }

    if (goType instanceof GoPointerType) {
      goType = ((GoPointerType)goType).getType();
      if (goType == null) return false;
    }

    return isValidType(goType.getUnderlyingType());
  }
}
