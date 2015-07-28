/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import com.goide.GoConstants;
import com.goide.psi.*;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoAssignmentNilWithoutExplicitType extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitVarDeclaration(@NotNull GoVarDeclaration o) {
        for (GoVarSpec spec : o.getVarSpecList()) {
          if (spec.getType() != null) continue;
          for (GoExpression expr : spec.getExpressionList()) {
            if (expr instanceof GoReferenceExpression) {
              if (((GoReferenceExpression)expr).getIdentifier().textMatches(GoConstants.NIL)) {
                holder.registerProblem(expr, "Cannot assign nil without explicit type", GENERIC_ERROR_OR_WARNING);
              }
            }
          }
        }
      }
    };
  }
}
