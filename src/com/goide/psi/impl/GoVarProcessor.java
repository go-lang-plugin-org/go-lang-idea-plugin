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

package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoVarProcessor extends GoScopeProcessorBase {
  public GoVarProcessor(String requestedName, PsiElement origin, boolean completion) {
    super(requestedName, origin, completion);
  }

  @Override
  protected boolean add(@NotNull GoNamedElement o) {
    boolean add = super.add(o);
    return PsiTreeUtil.getParentOfType(o, GoShortVarDeclaration.class) == null && add;
  }

  @NotNull
  @Override
  public List<GoNamedElement> getVariants() {
    return ContainerUtil.reverse(myResult);
  }

  protected boolean condition(@NotNull PsiElement psiElement) {
    return !(psiElement instanceof GoVarDefinition) &&
           !(psiElement instanceof GoParamDefinition) &&
           !(psiElement instanceof GoReceiver) &&
           !(psiElement instanceof GoFieldDefinition) &&
           !(psiElement instanceof GoAnonymousFieldDefinition) &&
           !(psiElement instanceof GoConstDefinition);
  }
}