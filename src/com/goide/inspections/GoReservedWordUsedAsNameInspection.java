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
import com.goide.sdk.GoSdkUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;

public class GoReservedWordUsedAsNameInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        check(o, holder);
      }

      @Override
      public void visitVarDefinition(@NotNull GoVarDefinition o) {
        check(o, holder);
      }
    };
  }

  private static void check(@NotNull GoFunctionDeclaration function, @NotNull ProblemsHolder holder) {
    GoFile builtin = GoSdkUtil.findBuiltinFile(function);
    if (builtin == null) return;

    String name = function.getName();
    if (name == null) return;

    for (GoFunctionDeclaration builtinFunctionDeclaration : builtin.getFunctions()) {
      if (name.equals(builtinFunctionDeclaration.getName())) {
        registerProblem(holder, function, builtinFunctionDeclaration, name);
        break;
      }
    }
  }

  private static void check(@NotNull GoVarDefinition variable, @NotNull ProblemsHolder holder) {
    GoFile builtin = GoSdkUtil.findBuiltinFile(variable);
    if (builtin == null) return;

    String name = variable.getName();
    if (name == null) return;

    for (GoTypeSpec builtinTypeDeclaration : builtin.getTypes()) {
      if (name.equals(builtinTypeDeclaration.getName())) {
        registerProblem(holder, variable, builtinTypeDeclaration, name);
        break;
      }
    }
  }

  private static void registerProblem(@NotNull ProblemsHolder holder,
                                      @NotNull GoNamedElement element,
                                      @NotNull GoNamedElement builtinElement,
                                      @NotNull String name) {
    PsiElement identifier = element.getIdentifier();
    if (identifier != null) {
      String elementDescription = ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
      String builtinElementDescription = ElementDescriptionUtil.getElementDescription(builtinElement, UsageViewTypeLocation.INSTANCE);
      String message = StringUtil.capitalize(elementDescription) + " '" + name + "' collides with builtin " + builtinElementDescription;
      holder.registerProblem(identifier, message, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new GoRenameQuickFix(element));
    }
  }
}
