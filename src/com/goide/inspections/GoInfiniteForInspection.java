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
import com.goide.quickfix.GoReplaceWithSelectStatementQuickFix;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoInfiniteForInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitForStatement(@NotNull GoForStatement o) {
        super.visitForStatement(o);

        if (o.getExpression() == null &&
            isEmpty(o.getBlock()) &&
            !hasRangeClause(o.getRangeClause()) &&
            !hasClause(o.getForClause())) {
          holder.registerProblem(o, "Infinite for loop", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new GoReplaceWithSelectStatementQuickFix());
        }
      }
    };
  }

  private static boolean isEmpty(@Nullable GoBlock block) {
    return block != null && block.getStatementList().isEmpty();
  }

  private static boolean hasRangeClause(@Nullable GoRangeClause rangeClause) {
    return rangeClause != null && !rangeClause.getExpressionList().isEmpty();
  }

  private static boolean hasClause(@Nullable GoForClause forClause) {
    return forClause != null && !forClause.getStatementList().isEmpty();
  }
}
