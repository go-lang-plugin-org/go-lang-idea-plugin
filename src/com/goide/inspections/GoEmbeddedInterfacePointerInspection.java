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
import org.jetbrains.annotations.NotNull;

public class GoEmbeddedInterfacePointerInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
        if (!(o.getType() instanceof GoPointerType)) return;
        GoTypeReferenceExpression reference = o.getTypeReferenceExpression();
        GoType goType = reference != null ? reference.resolveType() : null;
        if (!(goType instanceof GoSpecType)) return;

        if (!(((GoSpecType)goType).getType() instanceof GoInterfaceType)) return;

        holder.registerProblem(o, "Embedded type cannot be a pointer to interface", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
      }
    };
  }
}
