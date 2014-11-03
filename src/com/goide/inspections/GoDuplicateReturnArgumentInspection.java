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

import com.goide.psi.*;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GoDuplicateReturnArgumentInspection extends GoDuplicateArgumentInspection {
  @Override
  public void check(@Nullable GoSignature o, @NotNull ProblemsHolder holder) {
    if (o == null) return;
    Set<String> names = getParamNames(o);
    GoResult result = o.getResult();
    if (result == null) return;
    GoParameters parameters = result.getParameters();
    if (parameters == null) return;
    List<GoParameterDeclaration> list = parameters.getParameterDeclarationList();
    for (GoParameterDeclaration declaration : list) {
      for (GoParamDefinition parameter : declaration.getParamDefinitionList()) {
        String text = parameter.getIdentifier().getText();
        if ("_".equals(text)) continue;
        if (names.contains(text)) {
          holder.registerProblem(parameter, errorText(text), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
        else {
          names.add(text);
        }
      }
    }
  }

  @NotNull
  private static Set<String> getParamNames(@NotNull GoSignature o) {
    List<GoParameterDeclaration> params = o.getParameters().getParameterDeclarationList();
    Set<String> names = new LinkedHashSet<String>();
    for (GoParameterDeclaration fp : params) {
      for (GoParamDefinition parameter : fp.getParamDefinitionList()) {
        String text = parameter.getIdentifier().getText();
        if ("_".equals(text)) continue;
        names.add(text);
      }
    }
    return names;
  }
}
