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

// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.goide.psi.GoPsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.psi.*;

public class GoTypeSwitchStatementImpl extends GoSwitchStatementImpl implements GoTypeSwitchStatement {

  public GoTypeSwitchStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitTypeSwitchStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoStatement getStatement() {
    return GoPsiTreeUtil.getChildOfType(this, GoStatement.class);
  }

  @Override
  @NotNull
  public GoSwitchStart getSwitchStart() {
    return notNullChild(GoPsiTreeUtil.getChildOfType(this, GoSwitchStart.class));
  }

  @Override
  @NotNull
  public List<GoTypeCaseClause> getTypeCaseClauseList() {
    return GoPsiTreeUtil.getChildrenOfTypeAsList(this, GoTypeCaseClause.class);
  }

  @Override
  @NotNull
  public GoTypeSwitchGuard getTypeSwitchGuard() {
    return notNullChild(GoPsiTreeUtil.getChildOfType(this, GoTypeSwitchGuard.class));
  }

  @Override
  @Nullable
  public PsiElement getLbrace() {
    return findChildByType(LBRACE);
  }

  @Override
  @Nullable
  public PsiElement getRbrace() {
    return findChildByType(RBRACE);
  }

  @Override
  @Nullable
  public PsiElement getSemicolon() {
    return findChildByType(SEMICOLON);
  }

}
