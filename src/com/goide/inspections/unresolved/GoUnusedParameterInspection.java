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

package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.*;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoUnusedParameterInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitMethodDeclaration(@NotNull GoMethodDeclaration o) {
        super.visitMethodDeclaration(o);
        visitDeclaration(o);
      }

      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        super.visitFunctionDeclaration(o);
        visitDeclaration(o);
      }

      private void visitDeclaration(@NotNull GoFunctionOrMethodDeclaration o) {
        GoSignature signature = o.getSignature();
        if (signature == null) return;
        GoParameters parameters = signature.getParameters();
        visitParameterList(parameters.getParameterDeclarationList(), "parameter");
        GoResult result = signature.getResult();
        if (result == null) return;
        parameters = result.getParameters();
        if (parameters == null) return;
        visitParameterList(parameters.getParameterDeclarationList(), "named return parameter");
      }

      private void visitParameterList(List<GoParameterDeclaration> parameters, String what) {
        if (parameters.isEmpty()) return;

        for (GoParameterDeclaration parameterDeclaration : parameters) {
          for (GoParamDefinition parameter : parameterDeclaration.getParamDefinitionList()) {
            ProgressManager.checkCanceled();
            if (parameter.isBlank()) continue;

            Query<PsiReference> search = ReferencesSearch.search(parameter, parameter.getUseScope());
            if (search.findFirst() != null) continue;
            
            String paramName = parameter.getIdentifier().getText();
            holder.registerProblem(parameter, "Unused " + what + " '" + paramName + "'", ProblemHighlightType.LIKE_UNUSED_SYMBOL);
          }
        }
      }
    };
  }
}
