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

package com.goide.template;

import com.goide.GoLanguage;
import com.goide.GoTypes;
import com.goide.highlighting.GoSyntaxHighlighter;
import com.goide.psi.*;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class GoLiveTemplateContextType extends TemplateContextType {
  protected GoLiveTemplateContextType(@NotNull @NonNls String id,
                                      @NotNull String presentableName,
                                      @Nullable Class<? extends TemplateContextType> baseContextType) {
    super(id, presentableName, baseContextType);
  }

  @Override
  public boolean isInContext(@NotNull PsiFile file, int offset) {
    if (PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(GoLanguage.INSTANCE)) {
      PsiElement element = getFirstCompositeElement(ObjectUtils.notNull(file.findElementAt(offset), file));
      return element != null && isInContext(element);
    }

    return false;
  }

  @Nullable
  private static PsiElement getFirstCompositeElement(@Nullable PsiElement at) {
    if (at instanceof PsiComment || at instanceof LeafPsiElement && ((LeafPsiElement)at).getElementType() == GoTypes.STRING) return at;
    PsiElement result = at;
    while (result != null && (result instanceof PsiWhiteSpace || result.getNode().getChildren(null).length == 0)) {
      result = result.getParent();
    }
    return result;
  }

  protected abstract boolean isInContext(@NotNull PsiElement element);

  @Override
  public SyntaxHighlighter createHighlighter() {
    return new GoSyntaxHighlighter();
  }

  public static class File extends GoLiveTemplateContextType {
    protected File() {
      super("GO_FILE", "File", GoEverywhereContextType.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      if (element instanceof PsiComment) {
        return false;
      }
      return element instanceof GoFile || element.getParent() instanceof GoFile && !(element instanceof GoTopLevelDeclaration);
    }
  }

  public static class Type extends GoLiveTemplateContextType {
    protected Type() {
      super("GO_TYPE", "Type", GoEverywhereContextType.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return element instanceof GoType;
    }
  }

  public static class Block extends GoLiveTemplateContextType {
    protected Block() {
      super("GO_BLOCK", "Block", GoEverywhereContextType.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return (element instanceof GoLeftHandExprList || element instanceof GoSimpleStatement) &&
             PsiTreeUtil.getParentOfType(element, GoBlock.class) != null;
    }
  }

  public static class Expression extends GoLiveTemplateContextType {
    protected Expression() {
      super("GO_EXPRESSION", "Expression", GoEverywhereContextType.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return element instanceof GoExpression;
    }
  }

  public static class Tag extends GoLiveTemplateContextType {
    protected Tag() {
      super("GO_TAG", "Tag", GoEverywhereContextType.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return element instanceof GoStringLiteral && element.getParent() instanceof GoTag;
    }
  }

  public static class Statement extends GoLiveTemplateContextType {
    protected Statement() {
      super("GO_STATEMENT", "Statement", GoEverywhereContextType.class);
    }

    public static boolean onStatementBeginning(@NotNull PsiElement psiElement) {
      PsiElement prevLeaf = psiElement;
      while ((prevLeaf = PsiTreeUtil.prevLeaf(prevLeaf)) != null) {
        if (prevLeaf instanceof PsiComment || prevLeaf instanceof PsiErrorElement) {
          continue;
        }
        if (prevLeaf instanceof PsiWhiteSpace) {
          if (prevLeaf.textContains('\n')) {
            return true;
          }
          continue;
        }
        break;
      }
      if (prevLeaf == null) {
        return false;
      }
      IElementType type = prevLeaf.getNode().getElementType();
      return type == GoTypes.SEMICOLON || type == GoTypes.LBRACE || type == GoTypes.RBRACE || type == GoTypes.COLON;
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return !(element instanceof PsiComment) && onStatementBeginning(element);
    }
  }
}
