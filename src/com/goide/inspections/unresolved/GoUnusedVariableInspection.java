/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

public class GoUnusedVariableInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitVarDefinition(@NotNull GoVarDefinition o) {
        if (GoPsiImplUtil.isBlank(o.getIdentifier())) return;
        GoShortVarDeclaration shortDecl = PsiTreeUtil.getParentOfType(o, GoShortVarDeclaration.class);
        GoVarDeclaration decl = PsiTreeUtil.getParentOfType(o, GoVarDeclaration.class);
        if (shortDecl != null || decl != null) {
          PsiReference reference = o.getReference();
          PsiElement resolve = reference != null ? reference.resolve() : null;
          if (resolve != null) return;
          Query<PsiReference> search = ReferencesSearch.search(o, o.getUseScope());
          if (search.findFirst() == null) {
            boolean globalVar = decl != null && decl.getParent() instanceof GoFile;
            holder.registerProblem(o, "Unused variable " + "'" + o.getText() + "'",
                                   globalVar
                                   ? ProblemHighlightType.LIKE_UNUSED_SYMBOL
                                   : ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
          }
        }
      }
    };
  }
}
