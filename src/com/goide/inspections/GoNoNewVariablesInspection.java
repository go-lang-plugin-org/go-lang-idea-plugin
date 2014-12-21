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

package com.goide.inspections;

import com.goide.psi.GoShortVarDeclaration;
import com.goide.psi.GoVarDefinition;
import com.goide.psi.GoVisitor;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoNoNewVariablesInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitShortVarDeclaration(@NotNull GoShortVarDeclaration o) {
        List<GoVarDefinition> list = o.getVarDefinitionList();
        if (list.isEmpty()) return;

        GoVarDefinition first = ContainerUtil.getFirstItem(list);
        GoVarDefinition last = ContainerUtil.getLastItem(list);
        
        if (first == null || last == null) return;

        int start = first.getStartOffsetInParent();
        int end = last.getStartOffsetInParent() + last.getTextLength();

        for (GoVarDefinition def : list) {
          if (GoPsiImplUtil.isBlank(def)) continue;
          PsiReference reference = def.getReference();
          PsiElement resolve = reference != null ? reference.resolve() : null;
          if (resolve == null) return;
        }
        
        holder.registerProblem(o, TextRange.create(start, end), "No new variables on left side of :=");
      }
    };
  }
}
