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

import com.goide.psi.GoShortVarDeclaration;
import com.goide.psi.GoSimpleStatement;
import com.goide.psi.GoVarDefinition;
import com.goide.psi.GoVisitor;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoNoNewVariablesInspection extends GoInspectionBase {

  public static final String QUICK_FIX_NAME = "Replace with '='";

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitShortVarDeclaration(@NotNull GoShortVarDeclaration o) {
        List<GoVarDefinition> list = o.getVarDefinitionList();
        if (list.isEmpty()) return;

        GoVarDefinition first = ContainerUtil.getFirstItem(list);
        GoVarDefinition last = ContainerUtil.getLastItem(list);
        if (first == null || last == null) return;
        
        for (GoVarDefinition def : list) {
          if (def.isBlank()) continue;
          PsiReference reference = def.getReference();
          if (reference == null || reference.resolve() == null) return;
        }

        TextRange textRange = TextRange.create(first.getStartOffsetInParent(), last.getStartOffsetInParent() + last.getTextLength());
        holder.registerProblem(o, textRange, "No new variables on left side of :=", new MyLocalQuickFixBase());
      }
    };
  }

  private static class MyLocalQuickFixBase extends LocalQuickFixBase {
    private static final NotNullFunction<PsiElement, String> GET_TEXT_FUNCTION = new NotNullFunction<PsiElement, String>() {
      @NotNull
      @Override
      public String fun(@NotNull PsiElement element) {
        return element.getText();
      }
    };

    public MyLocalQuickFixBase() {
      super(QUICK_FIX_NAME);
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement element = descriptor.getStartElement();
      if (element.isValid() && element instanceof GoShortVarDeclaration) {
        PsiElement parent = element.getParent();
        if (parent instanceof GoSimpleStatement) {
          String left = StringUtil.join(((GoShortVarDeclaration)element).getVarDefinitionList(), GET_TEXT_FUNCTION, ", ");
          String right = StringUtil.join(((GoShortVarDeclaration)element).getExpressionList(), GET_TEXT_FUNCTION, ", ");
          parent.replace(GoElementFactory.createAssignmentStatement(project, left, right));
        }
      }
    }
  }
}
