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
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.progress.ProgressManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoMixedNamedUnnamedParametersInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitMethodDeclaration(@NotNull GoMethodDeclaration o) {
        super.visitMethodDeclaration(o);
        visitDeclaration(holder, o.getSignature(), "Method");
      }

      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        super.visitFunctionDeclaration(o);
        visitDeclaration(holder, o.getSignature(), "Function");
      }

      @Override
      public void visitFunctionLit(@NotNull GoFunctionLit o) {
        super.visitFunctionLit(o);
        visitDeclaration(holder, o.getSignature(), "Closure");
      }
    };
  }

  private static void visitDeclaration(@NotNull ProblemsHolder holder, @Nullable GoSignature signature, @NotNull String ownerType) {
    if (signature == null) return;
    GoParameters parameters = signature.getParameters();
    visitParameterList(holder, parameters, ownerType, "parameters");

    GoResult result = signature.getResult();
    parameters = result != null ? result.getParameters() : null;
    visitParameterList(holder, parameters, ownerType, "return parameters");
  }

  private static void visitParameterList(@NotNull ProblemsHolder holder, @Nullable GoParameters parameters,
                                         @NotNull String ownerType, @NotNull String what) {

    if (parameters == null || parameters.getParameterDeclarationList().isEmpty()) return;
    boolean hasNamed = false;
    boolean hasUnnamed = false;
    for (GoParameterDeclaration parameterDeclaration : parameters.getParameterDeclarationList()) {
      ProgressManager.checkCanceled();
      if (parameterDeclaration.getParamDefinitionList().isEmpty()) {
        hasUnnamed = true;
      }
      else {
        hasNamed = true;
      }

      if (hasNamed && hasUnnamed) {
        holder.registerProblem(parameters, ownerType + " has both named and unnamed " + what + " <code>#ref</code> #loc",
                               ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        return;
      }
    }
  }
}
