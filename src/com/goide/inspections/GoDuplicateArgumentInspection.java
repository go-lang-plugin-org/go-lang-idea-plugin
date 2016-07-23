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
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class GoDuplicateArgumentInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitCompositeElement(@NotNull GoCompositeElement o) {
        if (o instanceof GoSignatureOwner) {
          check(((GoSignatureOwner)o).getSignature(), holder);
        }
      }
    };
  }

  protected void check(@Nullable GoSignature o, @NotNull ProblemsHolder holder) {
    if (o != null) {
      checkParameters(holder, o.getParameters(), ContainerUtil.newLinkedHashSet());
    }
  }

  protected static void checkParameters(@NotNull ProblemsHolder holder,
                                        @NotNull GoParameters parameters,
                                        @NotNull Set<String> parameterNames) {
    for (GoParameterDeclaration fp : parameters.getParameterDeclarationList()) {
      for (GoParamDefinition parameter : fp.getParamDefinitionList()) {
        if (parameter.isBlank()) continue;
        String name = parameter.getName();
        if (name != null && parameterNames.contains(name)) {
          holder.registerProblem(parameter, "Duplicate argument <code>#ref</code> #loc", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
        else {
          parameterNames.add(name);
        }
      }
    }
  }
}
