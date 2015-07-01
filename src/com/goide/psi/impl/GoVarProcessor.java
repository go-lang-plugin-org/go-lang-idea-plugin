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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoVarProcessor extends GoScopeProcessorBase {
  private final boolean myImShortVarDeclaration;
  private final PsiElement myParentGuard; 
  @Nullable private final GoCompositeElement myScope;
  
  public GoVarProcessor(@NotNull String requestedName, @NotNull PsiElement origin, boolean completion) {
    this(requestedName, origin, completion, false);
  }

  public GoVarProcessor(@NotNull String requestedName, @NotNull PsiElement origin, boolean completion, boolean delegate) {
    super(requestedName, origin, completion);
    myImShortVarDeclaration = PsiTreeUtil.getParentOfType(origin, GoShortVarDeclaration.class) != null && !delegate;
    myParentGuard = origin.getParent() instanceof GoTypeSwitchGuard ? origin.getParent() : null;
    myScope = getScope(origin);
  }

  @Override
  protected boolean add(@NotNull GoNamedElement o) {
    if (PsiTreeUtil.findCommonParent(o, myOrigin) instanceof GoRangeClause) return true;
    PsiElement p = o.getParent();
    boolean inVarOrRange = PsiTreeUtil.getParentOfType(o, GoVarDeclaration.class) != null || p instanceof GoRangeClause;
    boolean differentBlocks = differentBlocks(o);
    boolean inShortVar = PsiTreeUtil.getParentOfType(o, GoShortVarDeclaration.class, GoRecvStatement.class) != null;
    if (inShortVar && differentBlocks && myImShortVarDeclaration) return true;
    if (differentBlocks && inShortVar && !inVarOrRange && getResult() != null && !myIsCompletion) return true;
    if (inShortVar && fromNotAncestorBlock(o)) return true;
    if (myParentGuard != null && o instanceof GoVarDefinition && p.isEquivalentTo(myParentGuard)) return true;
    return super.add(o);
  }

  private boolean fromNotAncestorBlock(@NotNull GoNamedElement o) {
    return (myScope instanceof GoExprCaseClause || myScope instanceof GoCommClause) &&
           !PsiTreeUtil.isAncestor(getScope(o), myOrigin, false);
  }

  private boolean differentBlocks(@Nullable GoNamedElement o) {
    return !Comparing.equal(myScope, getScope(o));
  }

  @Nullable
  private static GoCompositeElement getScope(@Nullable PsiElement o) {
    GoForStatement forStatement = PsiTreeUtil.getParentOfType(o, GoForStatement.class);
    if (forStatement != null) return forStatement.getBlock();
    GoIfStatement ifStatement = PsiTreeUtil.getParentOfType(o, GoIfStatement.class);
    if (ifStatement != null) return ifStatement.getBlock();
    GoElseStatement elseStatement = PsiTreeUtil.getParentOfType(o, GoElseStatement.class);
    if (elseStatement != null) return elseStatement.getBlock();
    GoExprCaseClause exprCaseClause = PsiTreeUtil.getParentOfType(o, GoExprCaseClause.class);
    if (exprCaseClause != null) return exprCaseClause;
    GoCommClause commClause = PsiTreeUtil.getParentOfType(o, GoCommClause.class);
    if (commClause != null) return commClause;
    return PsiTreeUtil.getParentOfType(o, GoBlock.class);
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