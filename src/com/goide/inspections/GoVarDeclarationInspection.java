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
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
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
        List<GoExpression> list = o.getExpressionList();
        List<GoVarDefinition> vars = o.getVarDefinitionList();

        if (list.size() == vars.size()) {
          checkExpressionShouldReturnOneResult(list, holder);
          return;
        }

        if (list.size() == 0 && !(o instanceof GoShortVarDeclaration)) return;
        checkVar(o, holder);
      }
    };
  }

  public static void checkVar(@NotNull GoVarSpec varDeclaration, @NotNull ProblemsHolder holder) {
    Pair<? extends List<? extends GoCompositeElement>, List<GoExpression>> p = getPair(varDeclaration);
    List<GoExpression> list = p.second;
    int idCount = p.first.size();
    int expressionsSize = list.size();
    if (idCount == expressionsSize) {
      checkExpressionShouldReturnOneResult(list, holder);
      return;
    }

    // var declaration could has no initialization expression, but short var declaration couldn't
    if (expressionsSize == 0 && !(varDeclaration instanceof GoShortVarDeclaration)) {
      return;
    }

    int exprCount = expressionsSize;

    if (varDeclaration instanceof GoRangeClause && idCount == 2) {
      // range clause can be assigned to two variables
      return;  
    }
    if (expressionsSize == 1) {
      exprCount = getExpressionResultCount(list.get(0));
      if (exprCount == UNKNOWN_COUNT || exprCount == idCount) return;
    }

    String msg = String.format("Assignment count mismatch: %d = %d", idCount, exprCount);
    holder.registerProblem(varDeclaration, msg, ProblemHighlightType.GENERIC_ERROR);
  }

  @NotNull
  private static Pair<List<? extends GoCompositeElement>, List<GoExpression>> getPair(@NotNull GoVarSpec varDeclaration) {
    PsiElement assign = varDeclaration.getAssign();
    if (assign == null) {
      return Pair.<List<? extends GoCompositeElement>, List<GoExpression>>create(ContainerUtil.<GoCompositeElement>emptyList(), ContainerUtil.<GoExpression>emptyList());
    }
    if (varDeclaration instanceof GoRecvStatement || varDeclaration instanceof GoRangeClause) {
      List<GoCompositeElement> v= ContainerUtil.newArrayList();
      List<GoExpression> e = ContainerUtil.newArrayList();
      for (PsiElement c : varDeclaration.getChildren()) {
        if (!(c instanceof GoCompositeElement)) continue;
        if (c.getTextOffset() < assign.getTextOffset()) {
          v.add(((GoCompositeElement)c));
        }
        else if (c instanceof GoExpression) {
          e.add(((GoExpression)c));
        }
      }
      return Pair.<List<? extends GoCompositeElement>, List<GoExpression>>create(v, e);
    }
    return Pair.<List<? extends GoCompositeElement>, List<GoExpression>>create(varDeclaration.getVarDefinitionList(), varDeclaration.getExpressionList());
  }
}
