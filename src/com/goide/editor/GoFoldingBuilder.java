/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.editor;

import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.psi.*;
import com.intellij.codeInsight.folding.CodeFoldingSettings;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.TokenType;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class GoFoldingBuilder extends FoldingBuilderEx implements DumbAware {
  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
    if (!(root instanceof GoFile)) return FoldingDescriptor.EMPTY;
    GoFile file = (GoFile)root;
    if (!file.isContentsLoaded()) return FoldingDescriptor.EMPTY;
    final List<FoldingDescriptor> result = ContainerUtil.newArrayList();

    GoImportList importList = ((GoFile)root).getImportList();
    if (importList != null) {
      GoImportDeclaration firstImport = ContainerUtil.getFirstItem(importList.getImportDeclarationList());
      if (firstImport != null) {
        PsiElement importKeyword = firstImport.getImport();
        int offset = importKeyword.getTextRange().getEndOffset();
        int startOffset = importKeyword.getNextSibling() instanceof PsiWhiteSpace ? offset + 1 : offset;
        TextRange range = TextRange.create(startOffset, importList.getTextRange().getEndOffset());
        if (range.getLength() > 3) {
          result.add(new FoldingDescriptor(importList, range));
        }
      }
    }

    for (GoFunctionOrMethodDeclaration method : ContainerUtil.concat(file.getMethods(), file.getFunctions())) {
      foldBlock(result, method.getBlock());
    }

    for (GoFunctionLit function : PsiTreeUtil.findChildrenOfType(file, GoFunctionLit.class)) {
      foldBlock(result, function.getBlock());
    }

    for (GoTypeSpec type : file.getTypes()) {
      foldTypes(type.getSpecType().getType(), result);
    }

    for (GoVarDeclaration varDeclaration : PsiTreeUtil.findChildrenOfType(file, GoVarDeclaration.class)) {
      TextRange range = processList(varDeclaration.getLparen(), varDeclaration.getRparen(), varDeclaration.getVarSpecList().size());
      if (range == null) {
        continue;
      }
      result.add(new FoldingDescriptor(varDeclaration, range));
    }

    for (GoTypeDeclaration typeDeclaration : PsiTreeUtil.findChildrenOfType(file, GoTypeDeclaration.class)) {
      TextRange range = processList(typeDeclaration.getLparen(), typeDeclaration.getRparen(), typeDeclaration.getTypeSpecList().size());
      if (range == null) {
        continue;
      }
      result.add(new FoldingDescriptor(typeDeclaration, range));
    }

    for (GoCompositeLit compositeLit : PsiTreeUtil.findChildrenOfType(file, GoCompositeLit.class)) {
      GoLiteralValue literalValue = compositeLit.getLiteralValue();
      TextRange range = processList(literalValue.getLbrace(), literalValue.getRbrace(), literalValue.getElementList().size());
      if (range == null) {
        continue;
      }
      result.add(new FoldingDescriptor(literalValue, range));
    }

    if (!quick) {
      final Set<PsiElement> processedComments = ContainerUtil.newHashSet();
      PsiTreeUtil.processElements(file, new PsiElementProcessor() {
        @Override
        public boolean execute(@NotNull PsiElement element) {
          IElementType type = element.getNode().getElementType();
          if (type == GoParserDefinition.MULTILINE_COMMENT && element.getTextRange().getLength() > 2) {
            result.add(new FoldingDescriptor(element, element.getTextRange()));
          }
          if (type == GoParserDefinition.LINE_COMMENT) {
            addCommentFolds(element, processedComments, result);
          }
          foldTypes(element, result); // folding for inner types
          return true;
        }
      });
    }
    return result.toArray(new FoldingDescriptor[result.size()]);
  }

  @Nullable
  private static TextRange processList(@Nullable PsiElement left, @Nullable PsiElement right, int size) {
    if (left == null || right == null || size < 2) {
      return null;
    }

    int startOffset = left.getTextRange().getStartOffset();
    int endOffset = right.getTextRange().getEndOffset();
    return TextRange.create(startOffset, endOffset);
  }

  private static void foldBlock(@NotNull List<FoldingDescriptor> result, @Nullable GoBlock block) {
    if (block != null && block.getTextRange().getLength() > 1) result.add(new FoldingDescriptor(block, block.getTextRange()));
  }

  private static void foldTypes(@Nullable PsiElement e, @NotNull List<FoldingDescriptor> result) {
    if (e instanceof GoStructType) {
      if (((GoStructType)e).getFieldDeclarationList().isEmpty()) return;
      addTypeBlock(e, ((GoStructType)e).getLbrace(), ((GoStructType)e).getRbrace(), result);
    }
    if (e instanceof GoInterfaceType) {
      if (e.getChildren().length == 0) return;
      addTypeBlock(e, ((GoInterfaceType)e).getLbrace(), ((GoInterfaceType)e).getRbrace(), result);
    }
  }

  private static void addTypeBlock(@NotNull PsiElement element,
                                   @Nullable PsiElement l,
                                   @Nullable PsiElement r,
                                   @NotNull List<FoldingDescriptor> result) {
    if (l != null && r != null) {
      result.add(new FoldingDescriptor(element, TextRange.create(l.getTextRange().getStartOffset(), r.getTextRange().getEndOffset())));
    }
  }

  // com.intellij.codeInsight.folding.impl.JavaFoldingBuilderBase.addCodeBlockFolds()
  private static void addCommentFolds(@NotNull PsiElement comment,
                                      @NotNull Set<PsiElement> processedComments,
                                      @NotNull List<FoldingDescriptor> foldElements) {
    if (processedComments.contains(comment)) return;

    PsiElement end = null;
    for (PsiElement current = comment.getNextSibling(); current != null; current = current.getNextSibling()) {
      ASTNode node = current.getNode();
      if (node == null) break;
      IElementType elementType = node.getElementType();
      if (elementType == GoParserDefinition.LINE_COMMENT) {
        end = current;
        processedComments.add(current);
        continue;
      }
      if (elementType == TokenType.WHITE_SPACE) continue;
      break;
    }

    if (end != null) {
      foldElements.add(new FoldingDescriptor(comment, new TextRange(comment.getTextRange().getStartOffset(), end.getTextRange().getEndOffset())));
    }
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    IElementType type = node.getElementType();
    if (psi instanceof GoBlock || psi instanceof GoStructType ||
        psi instanceof GoInterfaceType || psi instanceof GoLiteralValue) return "{...}";
    if (psi instanceof GoVarDeclaration || psi instanceof GoTypeDeclaration) return "(...)";
    if (psi instanceof GoImportDeclaration) return "...";
    if (GoParserDefinition.LINE_COMMENT == type) return "/.../";
    if (GoParserDefinition.MULTILINE_COMMENT == type) return "/*...*/";
    return null;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    IElementType type = node.getElementType();
    if (type == GoParserDefinition.LINE_COMMENT || type == GoParserDefinition.MULTILINE_COMMENT) {
      return CodeFoldingSettings.getInstance().COLLAPSE_DOC_COMMENTS;
    }
    if (type == GoTypes.BLOCK && CodeFoldingSettings.getInstance().COLLAPSE_METHODS) {
      ASTNode parent = node.getTreeParent();
      return parent != null && parent.getPsi() instanceof GoFunctionOrMethodDeclaration;
    }
    return CodeFoldingSettings.getInstance().COLLAPSE_IMPORTS && node.getElementType() == GoTypes.IMPORT_LIST;
  }
}
