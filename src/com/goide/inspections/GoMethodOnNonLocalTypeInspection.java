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
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

public class GoMethodOnNonLocalTypeInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitMethodDeclaration(@NotNull final GoMethodDeclaration method) {
        if (method.getIdentifier() == null || method.isBlank()) return;

        String containingFileImportPath = method.getContainingFile().getImportPath();
        if (containingFileImportPath == null) return;

        GoType methodType = method.getReceiver().getType();
        if (methodType == null ||
            methodType.getTypeReferenceExpression() == null) return;

        methodType = GoPsiImplUtil.findBaseTypeFromRef(methodType.getTypeReferenceExpression());
        if (methodType == null) return;

        String typeImportPath = ((GoFile)methodType.getContainingFile()).getImportPath();
        if (typeImportPath == null) return;

        if (typeImportPath.equals(containingFileImportPath)) return;

        holder.registerProblem(method.getIdentifier(), "Method defined on non-local type", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
      }
    };
  }
}
