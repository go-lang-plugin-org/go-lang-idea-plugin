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

package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolveUtil {
  private ResolveUtil() {}

  public static boolean treeWalkUp(@Nullable PsiElement place, @NotNull PsiScopeProcessor processor) {
    PsiElement lastParent = null;
    PsiElement run = place;
    while (run != null) {
      if (place != run && !run.processDeclarations(processor, ResolveState.initial(), lastParent, place)) return false;
      lastParent = run;
      run = run.getParent();
    }
    return true;
  }

  public static boolean processChildren(@NotNull PsiElement element,
                                        @NotNull PsiScopeProcessor processor,
                                        @NotNull ResolveState substitutor,
                                        @Nullable PsiElement lastParent,
                                        @NotNull PsiElement place) {
    PsiElement run = lastParent == null ? element.getLastChild() : lastParent.getPrevSibling();
    while (run != null) {
      if (run instanceof GoCompositeElement && !run.processDeclarations(processor, substitutor, null, place)) return false;
      run = run.getPrevSibling();
    }
    return true;
  }

  public static boolean processChildrenFromTop(@NotNull PsiElement element,
                                               @NotNull PsiScopeProcessor processor,
                                               @NotNull ResolveState substitutor,
                                               @Nullable PsiElement lastParent,
                                               @NotNull PsiElement place) {
    PsiElement run = element.getFirstChild();
    while (run != null) {
      if (run instanceof GoCompositeElement) {
        if (run.isEquivalentTo(lastParent)) return true;
        if (!run.processDeclarations(processor, substitutor, null, place)) return false;
      }
      run = run.getNextSibling();
    }
    return true;
  }
}