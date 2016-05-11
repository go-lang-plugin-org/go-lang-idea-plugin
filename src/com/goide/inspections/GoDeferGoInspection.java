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
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoDeferGoInspection extends GoInspectionBase {
  public static final String ADD_CALL_QUICK_FIX_NAME = "Add function call";
  public static final String UNWRAP_PARENTHESES_QUICK_FIX_NAME = "Unwrap parentheses";

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
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
        if (o == null || o instanceof GoCallExpr || o instanceof GoBuiltinCallExpr) return;
        if (o instanceof GoParenthesesExpr) {
          holder.registerProblem(o, "Expression in " + who + " must not be parenthesized", ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                 new GoUnwrapParensExpression());
        }
        LocalQuickFix[] fixes = o.getGoType(null) instanceof GoFunctionType ? new LocalQuickFix[]{new GoAddParensQuickFix()}
                                                                            : LocalQuickFix.EMPTY_ARRAY;
        holder.registerProblem(o, "Expression in " + who + " must be function call", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, fixes);
      }
    };
  }

  public static class GoAddParensQuickFix extends LocalQuickFixBase {
    protected GoAddParensQuickFix() {
      super(ADD_CALL_QUICK_FIX_NAME);
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      addParensIfNeeded(project, descriptor.getStartElement());
    }

    public static void addParensIfNeeded(@NotNull Project project, @Nullable PsiElement element) {
      if (element instanceof GoExpression && !(element instanceof GoCallExpr || element instanceof GoBuiltinCallExpr)) {
        if (((GoExpression)element).getGoType(null) instanceof GoFunctionType) {
          element.replace(GoElementFactory.createExpression(project, element.getText() + "()"));
        }
      }
    }
  }

  private static class GoUnwrapParensExpression extends LocalQuickFixBase {
    protected GoUnwrapParensExpression() {
      super(UNWRAP_PARENTHESES_QUICK_FIX_NAME);
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement element = descriptor.getStartElement();
      if (element instanceof GoParenthesesExpr) {
        GoExpression innerExpression = ((GoParenthesesExpr)element).getExpression();
        while (innerExpression instanceof GoParenthesesExpr) {
          innerExpression = ((GoParenthesesExpr)innerExpression).getExpression();
        }
        if (innerExpression != null) {
          element.replace(GoElementFactory.createExpression(project, innerExpression.getText()));
        }
      }
    }
  }
}
