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
import com.goide.psi.impl.GoExpressionUtil;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.quickfix.GoSimplifyBoolExprQuickFix;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoBoolExpressionsInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitAndExpr(@NotNull GoAndExpr o) {
        visitExpr(o, true);
      }

      @Override
      public void visitOrExpr(@NotNull GoOrExpr o) {
        visitExpr(o, false);
      }

      private void visitExpr(GoBinaryExpr o, boolean and) {
        if (!isTopmostOperationOfType(o, and)) return;

        List<GoExpression> elements = collect(o, and);
        for (int i = 0; i < elements.size(); i++) {
          GoExpression l = elements.get(i);
          if (l instanceof GoReferenceExpression && (l.textMatches("true") || l.textMatches("false")) &&
              GoPsiImplUtil.builtin(((GoReferenceExpression)l).resolve())) {
            registerProblem(o, holder);
            return;
          }
          for (int j = i + 1; j < elements.size(); j++) {
            ProgressManager.checkCanceled();
            GoExpression r = elements.get(j);
            if (isEqualsWithNot(l, r) || isEqualsWithNot(r, l) || GoExpressionUtil.identical(l, r)) {
              registerProblem(o, holder);
              return;
            }
            // todo expr evaluating! x != c1 || x != c2 (c1, c2 const, c1 != c2)
          }
        }
      }

      private boolean isTopmostOperationOfType(GoBinaryExpr o, boolean isAndType) {
        PsiElement parent = PsiTreeUtil.skipParentsOfType(o, GoParenthesesExpr.class);
        return !(parent instanceof GoBinaryExpr) || !isSameOp((GoExpression)parent, isAndType);
      }
    };
  }

  private static void registerProblem(@NotNull GoBinaryExpr o, @NotNull ProblemsHolder holder) {
    holder.registerProblem(o, "Simplify", new GoSimplifyBoolExprQuickFix(o));
  }

  public static boolean isEqualsWithNot(@Nullable GoExpression not, @Nullable GoExpression expr) {
    return not instanceof GoUnaryExpr &&
           ((GoUnaryExpr)not).getNot() != null &&
           GoExpressionUtil.identical(((GoUnaryExpr)not).getExpression(), expr);
  }

  @NotNull
  public static List<GoExpression> collect(GoBinaryExpr o, boolean and) {
    List<GoExpression> result = ContainerUtil.newSmartList();
    result.addAll(processExpr(o.getLeft(), and));
    result.addAll(processExpr(o.getRight(), and));
    return result;
  }

  @NotNull
  private static List<GoExpression> processExpr(@Nullable GoExpression e, boolean and) {
    if (e == null) return ContainerUtil.emptyList();
    if (isSameOp(e, and)) {
      return collect((GoBinaryExpr)e, and);
    }
    if (e instanceof GoParenthesesExpr) {
      return processExpr(((GoParenthesesExpr)e).getExpression(), and);
    }
    return Collections.singletonList(e);
  }

  private static boolean isSameOp(GoExpression expr, boolean and) {
    return and ? expr instanceof GoAndExpr : expr instanceof GoOrExpr;
  }
}
