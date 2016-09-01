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
      PsiElement psiElement = ObjectUtils.notNull(file.findElementAt(offset), file);
      if (!acceptLeaf()) {
        psiElement = getFirstCompositeElement(psiElement);
      }
      return psiElement != null && isInContext(psiElement);
    }

    return false;
  }

  protected boolean acceptLeaf() {
    return false;
  }

  @Nullable
  public static PsiElement prevVisibleLeafOrNewLine(PsiElement element) {
    PsiElement prevLeaf = element;
    while ((prevLeaf = PsiTreeUtil.prevLeaf(prevLeaf)) != null) {
      if (prevLeaf instanceof PsiComment || prevLeaf instanceof PsiErrorElement) {
        continue;
      }
      if (prevLeaf instanceof PsiWhiteSpace) {
        if (prevLeaf.textContains('\n')) {
          return prevLeaf;
        }
        continue;
      }
      break;
    }
    return prevLeaf;
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
      if (element instanceof PsiComment || element instanceof GoPackageClause) {
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
      if (element.getNode().getElementType() == GoTypes.IDENTIFIER) {
        if (isInsideFieldTypeDeclaration(element)) {
          return true;
        }
        if (isInsideFieldTypeDeclaration(prevVisibleLeafOrNewLine(element))) {
          return true;
        }
      }
      return false;
    }

    private static boolean isInsideFieldTypeDeclaration(@Nullable PsiElement element) {
      if (element != null) {
        PsiElement parent = element.getParent();
        if (parent instanceof GoTypeReferenceExpression) {
          return PsiTreeUtil.skipParentsOfType(parent, GoType.class) instanceof GoFieldDeclaration;
        }
      }
      return false;
    }

    @Override
    protected boolean acceptLeaf() {
      return true;
    }
  }

  public static class TagLiteral extends GoLiveTemplateContextType {
    protected TagLiteral() {
      super("GO_TAG_LITERAL", "Tag literal", GoEverywhereContextType.class);
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
      PsiElement prevLeaf = prevVisibleLeafOrNewLine(psiElement);
      if (prevLeaf == null) {
        return false;
      }
      if (prevLeaf instanceof PsiWhiteSpace) {
        return true;
      }
      IElementType type = prevLeaf.getNode().getElementType();
      return type == GoTypes.SEMICOLON || type == GoTypes.LBRACE || type == GoTypes.RBRACE || type == GoTypes.COLON;
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      if (element instanceof PsiComment) {
        return false;
      }

      PsiElement parent = element.getParent();
      if (parent instanceof PsiErrorElement || parent instanceof GoExpression) {
        parent = parent.getParent();
      }
      return (parent instanceof GoStatement || parent instanceof GoLeftHandExprList || parent instanceof GoBlock) 
             && onStatementBeginning(element);
    }

    @Override
    protected boolean acceptLeaf() {
      return true;
    }
  }
}
