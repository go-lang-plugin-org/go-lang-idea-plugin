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

package com.goide.quickfix;

import com.goide.psi.GoAndExpr;
import com.goide.psi.GoExpression;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class GoSimplifyBoolExprQuickFix extends LocalQuickFixOnPsiElement {

  public static final String QUICK_FIX_NAME = "Simplify expression";

  private GoSimplifyBoolExprQuickFix(@NotNull PsiElement element) {
    super(element);
  }

  @Override
  @NotNull
  public String getFamilyName() {
    return getName();
  }

  @NotNull
  @Override
  public String getText() {
    return "Simplify expression";
  }


  public static class RemoveRedundantExpressions extends GoSimplifyBoolExprQuickFix {
    private final List<GoExpression> myExpressions;
    private final List<GoExpression> myToRemove;

    public RemoveRedundantExpressions(@NotNull PsiElement element,
                                      @NotNull List<GoExpression> expressions,
                                      @NotNull List<GoExpression> toRemove) {
      super(element);
      myExpressions = expressions;
      myToRemove = toRemove;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
      for (GoExpression e : myToRemove) {
        myExpressions.remove(e);
      }
      String separator = startElement instanceof GoAndExpr ? " && " : " || ";
      String text = StringUtil.join(myExpressions, GoPsiImplUtil.GET_TEXT_FUNCTION, separator);
      GoExpression expression = GoElementFactory.createExpression(project, text);
      startElement.replace(expression);
    }
  }

  public static class ReplaceAllByBoolConst extends GoSimplifyBoolExprQuickFix {

    private final boolean trueVal;

    public ReplaceAllByBoolConst(@NotNull PsiElement element, boolean trueVal) {
      super(element);
      this.trueVal = trueVal;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
      GoExpression expression = GoElementFactory.createExpression(project, trueVal ? "true" : "false");
      startElement.replace(expression);
    }
  }
}
