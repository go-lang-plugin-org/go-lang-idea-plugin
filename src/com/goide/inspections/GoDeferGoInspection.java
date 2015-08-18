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
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoDeferGoInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitDeferStatement(@NotNull GoDeferStatement o) {
        super.visitDeferStatement(o);
        checkExpression(o.getExpression(), "defer");
      }

      @Override
      public void visitGoStatement(@NotNull GoGoStatement o) {
        super.visitGoStatement(o);
        checkExpression(o.getExpression(), "go");
      }

      private void checkExpression(@Nullable GoExpression o, String who) {
        if (o == null) return;
        if (o instanceof GoCallExpr || o instanceof GoBuiltinCallExpr) return;
        holder.registerProblem(o, "Argument to " + who + " must be function call", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
      }
    };
  }
}
