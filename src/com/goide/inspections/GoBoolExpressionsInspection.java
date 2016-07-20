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
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoBoolExpressionsInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitAndExpr(@NotNull GoAndExpr o) {
        visitExpr(o);
      }

      @Override
      public void visitOrExpr(@NotNull GoOrExpr o) {
        visitExpr(o);
      }

      private void visitExpr(GoBinaryExpr o) {
        boolean and = o instanceof GoAndExpr;

        PsiElement parent = o.getParent();
        while (parent instanceof GoParenthesesExpr) parent = parent.getParent();
        if (parent instanceof GoBinaryExpr && isSameOp((GoExpression)parent, and)) return;

        List<GoExpression> elements = collect(o, and);
        List<GoExpression> toRemove = ContainerUtil.newSmartList();
        for (int i = 0; i < elements.size(); i++) {
          GoExpression l = elements.get(i);
          if (l instanceof GoReferenceExpression &&
              (l.textMatches("true") || l.textMatches("false")) &&
              GoPsiImplUtil.builtin(((GoReferenceExpression)l).resolve())) {
            boolean trueExpr = l.textMatches("true");
            if (and ^ !trueExpr) {
              toRemove.add(l);
            }
            else {
              replaceExprByConst(o, trueExpr);
            }
          }
          for (int j = i + 1; j < elements.size(); j++) {
            GoExpression r = elements.get(j);
            if (isEqualsWithNot(l, r) || isEqualsWithNot(r, l)) {
              replaceExprByConst(o, !and); // and -> false; or -> true
              return;
            }
            if (GoExpressionUtil.identical(l, r)) {
              toRemove.add(l);
              break;
            }
            // todo expr evaluating! x != c1 || x != c2 (c1, c2 const, c1 != c2)
          }
        }
        if (!toRemove.isEmpty()) {
          holder.registerProblem(o, "Redundant expression", new GoSimplifyBoolExprQuickFix.RemoveRedundantExpressions(o, elements, toRemove));
        }
      }

      private void replaceExprByConst(GoBinaryExpr o, boolean trueExpr) { // todo: what to do if true or false redefined?
        holder.registerProblem(o, "Redundant expression", new GoSimplifyBoolExprQuickFix.ReplaceAllByBoolConst(o, trueExpr));
      }

      private boolean isEqualsWithNot(@Nullable GoExpression not, @Nullable GoExpression expr) {
        return not instanceof GoUnaryExpr &&
               ((GoUnaryExpr)not).getNot() != null &&
               GoExpressionUtil.identical(((GoUnaryExpr)not).getExpression(), expr);
      }

      @NotNull
      private List<GoExpression> collect(GoBinaryExpr o, boolean and) {
        List<GoExpression> result = ContainerUtil.newSmartList();
        result.addAll(processExpr(o.getLeft(), and));
        result.addAll(processExpr(o.getRight(), and));
        return result;
      }

      @NotNull
      private List<GoExpression> processExpr(@Nullable GoExpression e, boolean and) {
        if (e == null) return ContainerUtil.emptyList();
        if (isSameOp(e, and)) {
          return collect((GoBinaryExpr)e, and);
        }
        if (e instanceof GoParenthesesExpr) {
          return processExpr(((GoParenthesesExpr)e).getExpression(), and);
        }
        return Collections.singletonList(e);
      }

      private boolean isSameOp(GoExpression expr, boolean and) {
        return and ? expr instanceof GoAndExpr : expr instanceof GoOrExpr;
      }
    };
  }
}
