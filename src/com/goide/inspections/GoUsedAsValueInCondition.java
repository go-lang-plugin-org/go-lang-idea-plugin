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
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.NotNullFunction;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoUsedAsValueInCondition extends GoInspectionBase {
  public static final String QUICK_FIX_NAME = "Convert to '==''";

  private static final NotNullFunction<PsiElement, String> GET_TEXT_FUNCTION = new NotNullFunction<PsiElement, String>() {
    @NotNull
    @Override
    public String fun(@NotNull PsiElement element) {
      return element.getText();
    }
  };

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {

      @Override
      public void visitAssignmentStatement(@NotNull GoAssignmentStatement o) {
        if (o.getParent() != null &&
            o.getParent() instanceof GoIfStatement &&
            ((GoIfStatement)o.getParent()).getExpression() == null) {
          String left = StringUtil.join(o.getLeftHandExprList().getExpressionList(), GET_TEXT_FUNCTION, ", ");
          String right = StringUtil.join(o.getExpressionList(), GET_TEXT_FUNCTION, ", ");

          holder.registerProblem(o, left + " = " + right + " used as value", GENERIC_ERROR_OR_WARNING, new GoAssignmentToComparisonQuickFix());
        }
      }
      @Override
      public void visitShortVarDeclaration(@NotNull GoShortVarDeclaration o) {
        if (o.getParent() != null &&
            o.getParent().getParent() != null &&
            o.getParent().getParent() instanceof GoIfStatement &&
            ((GoIfStatement)o.getParent().getParent()).getExpression() == null) {

          String left = StringUtil.join(o.getVarDefinitionList(), GET_TEXT_FUNCTION, ", ");
          String right = StringUtil.join(o.getExpressionList(), GET_TEXT_FUNCTION, ", ");

          holder.registerProblem(o, left + " := " + right + " used as value", GENERIC_ERROR_OR_WARNING, new GoAssignmentToComparisonQuickFix());
        }
      }
    };
  }

  private static class GoAssignmentToComparisonQuickFix extends LocalQuickFixBase {
    private GoAssignmentToComparisonQuickFix() {
      super(QUICK_FIX_NAME);
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      final PsiElement element = descriptor.getPsiElement();
      if (!(element instanceof GoAssignmentStatement) &&
          !(element instanceof GoShortVarDeclaration)) {
        return;
      }

      String left, right;

      if (element instanceof GoAssignmentStatement) {
        left = StringUtil.join(((GoAssignmentStatement)element).getLeftHandExprList().getExpressionList(), GET_TEXT_FUNCTION, ", ");
        right = StringUtil.join(((GoAssignmentStatement)element).getExpressionList(), GET_TEXT_FUNCTION, ", ");
        element.replace(GoElementFactory.createExpression(project, left + " == " + right));
      } else {
        left = StringUtil.join(((GoShortVarDeclaration)element).getVarDefinitionList(), GET_TEXT_FUNCTION, ", ");
        right = StringUtil.join(((GoShortVarDeclaration)element).getExpressionList(), GET_TEXT_FUNCTION, ", ");
      }

      element.replace(GoElementFactory.createComparison(project, left + " == " + right));
    }
  }
}
