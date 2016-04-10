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
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoMethodOnNonLocalTypeInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitMethodDeclaration(@NotNull GoMethodDeclaration method) {
        if (method.getIdentifier() == null || method.isBlank()) return;
        GoFile methodContainingFile = method.getContainingFile();
        String methodImportPath = methodContainingFile.getImportPath(false);
        if (methodImportPath == null) return;
        
        GoType methodType = getMethodType(method);
        if (methodType == null) return;
        GoFile typeContainingFile = (GoFile)methodType.getContainingFile();
        String typeImportPath = typeContainingFile.getImportPath(false);
        if (typeImportPath == null) return;

        String methodPackageName = methodContainingFile.getPackageName();
        String typePackageName = typeContainingFile.getPackageName();
        if (!StringUtil.equals(methodPackageName, typePackageName)  || !typeImportPath.equals(methodImportPath)) {
          holder.registerProblem(method.getIdentifier(), "Method defined on non-local type", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
      }

      @Nullable
      private GoType getMethodType(@NotNull GoMethodDeclaration method) {
        GoType methodType = method.getReceiverType();
        GoTypeReferenceExpression ref = methodType == null ? null : GoPsiImplUtil.getTypeReference(methodType);
        return ref == null ? null : ref.resolveType();
      }
    };
  }
}
