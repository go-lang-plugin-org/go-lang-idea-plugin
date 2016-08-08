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
import com.goide.quickfix.GoRenameQuickFix;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;

public class GoImportUsedAsNameInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitTypeSpec(@NotNull GoTypeSpec o) {
        super.visitTypeSpec(o);
        check(o, holder);
      }

      @Override
      public void visitConstDefinition(@NotNull GoConstDefinition o) {
        super.visitConstDefinition(o);
        check(o, holder);
      }

      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        super.visitFunctionDeclaration(o);
        check(o, holder);
      }

      @Override
      public void visitVarDefinition(@NotNull GoVarDefinition o) {
        super.visitVarDefinition(o);
        check(o, holder);
      }
    };
  }

  private static void check(@NotNull GoNamedElement element, @NotNull ProblemsHolder holder) {
    String name = element.getName();
    if (StringUtil.isNotEmpty(name) &&
        !"_".equals(name) &&
        element.getContainingFile().getImportMap().containsKey(name)) {
      registerProblem(holder, element);
    }
  }

  private static void registerProblem(@NotNull ProblemsHolder holder, @NotNull GoNamedElement element) {
    PsiElement identifier = element.getIdentifier();
    if (identifier != null) {
      String elementDescription = ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
      String message = StringUtil.capitalize(elementDescription) + " <code>#ref</code> collides with imported package name #loc";
      holder.registerProblem(identifier, message, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new GoRenameQuickFix(element));
    }
  }
}
