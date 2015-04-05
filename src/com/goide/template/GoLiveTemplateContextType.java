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

package com.goide.template;

import com.goide.GoLanguage;
import com.goide.GoTypes;
import com.goide.highlighting.GoSyntaxHighlighter;
import com.goide.psi.*;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class GoLiveTemplateContextType extends TemplateContextType {
  protected GoLiveTemplateContextType(@NotNull @NonNls String id, @NotNull String presentableName, @Nullable Class<? extends TemplateContextType> baseContextType) {
    super(id, presentableName, baseContextType);
  }

  public boolean isInContext(@NotNull final PsiFile file, final int offset) {
    if (PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(GoLanguage.INSTANCE)) {
      PsiElement element = getFirstCompositeElement(file.findElementAt(offset));
      return element != null && isInContext(element);
    }

    return false;
  }

  @Nullable
  private static PsiElement getFirstCompositeElement(@Nullable PsiElement at) {
    if (at instanceof PsiComment || at instanceof LeafPsiElement && ((LeafPsiElement)at).getElementType() == GoTypes.STRING) return at;
    PsiElement result = at;
    while (result != null && (result instanceof PsiWhiteSpace || result.getChildren().length == 0)) {
      result = result.getParent();
    }
    return result;
  }

  protected abstract boolean isInContext(@NotNull PsiElement element);

  public SyntaxHighlighter createHighlighter() {
    return new GoSyntaxHighlighter();
  }

  public static class GoFileContextType extends GoLiveTemplateContextType {
    protected GoFileContextType() {
      super("GO_FILE", "Go file", GoEverywhereContextType.class);
    }
  
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return element.getParent() instanceof GoFile && !(element instanceof GoTopLevelDeclaration);
    }
  }
  
  public static class GoTypeContextType extends GoLiveTemplateContextType {
    protected GoTypeContextType() {
      super("GO_TYPE", "Go type", GoEverywhereContextType.class);
    }
  
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return element instanceof GoType;
    }
  }
  
  public static class GoBlockContextType extends GoLiveTemplateContextType {
    protected GoBlockContextType() {
      super("GO_BLOCK", "Go block", GoEverywhereContextType.class);
    }
  
    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return (element instanceof GoLeftHandExprList || element instanceof GoSimpleStatement) && PsiTreeUtil.getParentOfType(element, GoBlock.class) != null;
    }
  }
}
