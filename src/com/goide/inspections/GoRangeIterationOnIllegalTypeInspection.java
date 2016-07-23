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

import com.goide.psi.GoExpression;
import com.goide.psi.GoRangeClause;
import com.goide.psi.GoType;
import com.goide.psi.GoVisitor;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

public class GoRangeIterationOnIllegalTypeInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitRangeClause(@NotNull GoRangeClause o) {
        super.visitRangeClause(o);
        GoExpression expression = o.getRangeExpression();
        GoType type = expression != null ? expression.getGoType(null) : null;
        if (type != null && !GoTypeUtil.isIterable(type)) {
          holder.registerProblem(expression, "Cannot range over data (type " + GoPsiImplUtil.getText(type) + ")",
                                 ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
      }
    };
  }
}
