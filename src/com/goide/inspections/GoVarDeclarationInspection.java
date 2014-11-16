/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

import java.util.List;

import static com.goide.inspections.GoInspectionUtil.*;

public class GoVarDeclarationInspection extends GoInspectionBase {

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitVarSpec(@NotNull GoVarSpec o) {
        List<GoExpression> exprs = o.getExpressionList();
        List<GoVarDefinition> vars = o.getVarDefinitionList();

        if (exprs.size() == vars.size()) {
          checkExpressionShouldReturnOneResult(exprs, holder);
          return;
        }

        if (exprs.size() == 0 && !(o instanceof GoShortVarDeclaration)) {
          return;
        }

        checkVar(o, holder);
      }
    };
  }

  public static void checkVar(GoVarSpec varDeclaration, ProblemsHolder holder) {
    List<GoVarDefinition> ids = varDeclaration.getVarDefinitionList();
    List<GoExpression> exprs = varDeclaration.getExpressionList();
    if (ids.size() == exprs.size()) {
      checkExpressionShouldReturnOneResult(exprs, holder);
      return;
    }

    // var declaration could has no initialization expression, but short var declaration couldn't
    if (exprs.size() == 0 && !(varDeclaration instanceof GoShortVarDeclaration)) {
      return;
    }

    int idCount = ids.size();
    int exprCount = exprs.size();

    if (exprs.size() == 1) {
      exprCount = getExpressionResultCount(exprs.get(0));
      if (exprCount == UNKNOWN_COUNT || exprCount == idCount) {
        return;
      }
    }

    String msg = String.format("Assignment count mismatch: %d = %d", idCount, exprCount);
    holder.registerProblem(varDeclaration, msg, ProblemHighlightType.GENERIC_ERROR);
  }
}
