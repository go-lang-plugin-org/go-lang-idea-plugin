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
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    checkParameters(holder, parameters, names);
  }

  @NotNull
  private static Set<String> getParamNames(@NotNull GoSignature o) {
    List<GoParameterDeclaration> params = o.getParameters().getParameterDeclarationList();
    Set<String> names = ContainerUtil.newLinkedHashSet();
    for (GoParameterDeclaration fp : params) {
      for (GoParamDefinition parameter : fp.getParamDefinitionList()) {
        if (parameter.isBlank()) continue;
        ContainerUtil.addIfNotNull(names, parameter.getName());
      }
    }
    return names;
  }
}
