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
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.goide.inspections.GoInspectionUtil.UNKNOWN_COUNT;
import static com.goide.inspections.GoInspectionUtil.getExpressionResultCount;

public class GoVarDeclarationInspection extends GoInspectionBase {
  @NotNull
  private static Pair<List<? extends GoCompositeElement>, List<GoExpression>> getPair(@NotNull GoVarSpec varDeclaration) {
    PsiElement assign = varDeclaration instanceof GoShortVarDeclaration ? ((GoShortVarDeclaration)varDeclaration).getVarAssign()
                                                                        : varDeclaration.getAssign();
    if (assign == null) {
      return Pair.create(ContainerUtil.emptyList(), ContainerUtil.emptyList());
    }
    if (varDeclaration instanceof GoRecvStatement) {
      return Pair.create(((GoRecvStatement)varDeclaration).getLeftExpressionsList(), varDeclaration.getRightExpressionsList());
    }
    if (varDeclaration instanceof GoRangeClause) {
      return Pair.create(((GoRangeClause)varDeclaration).getLeftExpressionsList(), varDeclaration.getRightExpressionsList());
    }
    return Pair.create(varDeclaration.getVarDefinitionList(), varDeclaration.getRightExpressionsList());
  }

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitAssignmentStatement(@NotNull GoAssignmentStatement o) {
        validatePair(o, Pair.create(o.getLeftHandExprList().getExpressionList(), o.getExpressionList()));
      }

      @Override
      public void visitVarSpec(@NotNull GoVarSpec o) {
        validatePair(o, getPair(o));
      }

      private void validatePair(@NotNull GoCompositeElement o, Pair<? extends List<? extends GoCompositeElement>, List<GoExpression>> p) {
        List<GoExpression> list = p.second;
        int idCount = p.first.size();
        for (GoCompositeElement idElement : p.first) {
          if (idElement instanceof GoIndexOrSliceExpr) {
            GoType referenceType = GoPsiImplUtil.getIndexedExpressionReferenceType((GoIndexOrSliceExpr)idElement, null);
            if (GoTypeUtil.isString(referenceType)) {
              // https://golang.org/ref/spec#Index_expressions
              // For a of string type: a[x] may not be assigned to
              holder.registerProblem(idElement, "Cannot assign to <code>#ref</code> #loc");
            }
          }
        }

        int expressionsSize = list.size();
        if (expressionsSize == 0) {
          return;
        }
        if (idCount == expressionsSize) {
          checkExpressionShouldReturnOneResult(list, holder);
          return;
        }

        int exprCount = expressionsSize;
        if (idCount == 2) {
          if (o instanceof GoRangeClause) {
            // range clause can be assigned to two variables
            return;
          }
          if (expressionsSize == 1) {
            GoExpression expression = list.get(0);
            if (expression instanceof GoIndexOrSliceExpr) {
              GoType referenceType = GoPsiImplUtil.getIndexedExpressionReferenceType((GoIndexOrSliceExpr)expression, null);
              if (referenceType != null && referenceType.getUnderlyingType() instanceof GoMapType) {
                // index expressions on maps can be assigned to two variables
                return;
              }
            }
          }
        }
        if (expressionsSize == 1) {
          exprCount = getExpressionResultCount(list.get(0));
          if (exprCount == UNKNOWN_COUNT || exprCount == idCount) return;
        }

        String msg = String.format("Assignment count mismatch: %d = %d", idCount, exprCount);
        holder.registerProblem(o, msg, ProblemHighlightType.GENERIC_ERROR);
      }
    };
  }

  private static void checkExpressionShouldReturnOneResult(@NotNull List<GoExpression> expressions, @NotNull ProblemsHolder result) {
    for (GoExpression expr : expressions) {
      int count = getExpressionResultCount(expr);
      if (count != UNKNOWN_COUNT && count != 1) {
        String text = expr.getText();
        if (expr instanceof GoCallExpr) {
          text = ((GoCallExpr)expr).getExpression().getText();
        }

        String msg = count == 0 ? text + "() doesn't return a value" : "Multiple-value " + text + "() in single-value context";
        result.registerProblem(expr, msg, ProblemHighlightType.GENERIC_ERROR);
      }
    }
  }
}
