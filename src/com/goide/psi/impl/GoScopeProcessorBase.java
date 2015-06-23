/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoNamedElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GoScopeProcessorBase extends GoScopeProcessor {
  @NotNull protected final OrderedSet<GoNamedElement> myResult = new OrderedSet<GoNamedElement>();

  @NotNull protected final PsiElement myOrigin;
  @NotNull private final String myRequestedName;
  protected final boolean myIsCompletion;

  public GoScopeProcessorBase(@NotNull String requestedName, @NotNull PsiElement origin, boolean completion) {
    myRequestedName = requestedName;
    myOrigin = origin;
    myIsCompletion = completion;
  }

  @Override
  public boolean execute(@NotNull PsiElement psiElement, @NotNull ResolveState resolveState) {
    if (psiElement instanceof GoFunctionOrMethodDeclaration) return false;
    if (!(psiElement instanceof GoNamedElement)) return true;
    if (!myIsCompletion && !myRequestedName.equals(((GoNamedElement)psiElement).getName())) return true;
    if (condition(psiElement)) return true;
    if (psiElement.equals(myOrigin)) return true;
    return add((GoNamedElement)psiElement) || myIsCompletion;
  }

  protected boolean add(@NotNull GoNamedElement psiElement) {
    return !myResult.add(psiElement);
  }

  @Nullable
  public GoNamedElement getResult() {
    return ContainerUtil.getFirstItem(myResult);
  }

  @NotNull
  public List<GoNamedElement> getVariants() {
    return myResult;
  }

  protected abstract boolean condition(@NotNull PsiElement element);
}
