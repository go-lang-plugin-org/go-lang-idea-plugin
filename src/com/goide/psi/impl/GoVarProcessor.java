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
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoVarProcessor extends GoScopeProcessorBase {
  private final boolean myImShortVarDeclaration;
  @Nullable private final GoCompositeElement myScope;
  
  public GoVarProcessor(@NotNull String requestedName, @NotNull PsiElement origin, boolean completion) {
    this(requestedName, origin, completion, false);
  }

  public GoVarProcessor(@NotNull String requestedName, @NotNull PsiElement origin, boolean completion, boolean delegate) {
    super(requestedName, origin, completion);
    myImShortVarDeclaration = PsiTreeUtil.getParentOfType(origin, GoShortVarDeclaration.class) != null && !delegate;
    myScope = getScope(origin);
  }

  @Override
  protected boolean add(@NotNull GoNamedElement o) {
    if (PsiTreeUtil.findCommonParent(o, myOrigin) instanceof GoRangeClause) return true;
    boolean inVarOrRange = PsiTreeUtil.getParentOfType(o, GoVarDeclaration.class) != null || o.getParent() instanceof GoRangeClause;
    boolean differentBlocks = differentBlocks(o);
    boolean inShortVar = PsiTreeUtil.getParentOfType(o, GoShortVarDeclaration.class, GoRecvStatement.class) != null;
    if (inShortVar && differentBlocks && myImShortVarDeclaration) return true;
    if (differentBlocks && inShortVar && !inVarOrRange && getResult() != null) return true;
    return super.add(o) || !inVarOrRange;
  }

  private boolean differentBlocks(@Nullable GoNamedElement o) {
    return !Comparing.equal(myScope, getScope(o));
  }

  @Nullable
  private static GoCompositeElement getScope(@Nullable PsiElement o) {
    GoForStatement forStatement = PsiTreeUtil.getParentOfType(o, GoForStatement.class);
    if (forStatement != null) return forStatement.getBlock();
    return PsiTreeUtil.getParentOfType(o, GoBlock.class);
  }

  @Nullable
  @Override
  public GoNamedElement getResult() {
    return ContainerUtil.getLastItem(myResult);
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