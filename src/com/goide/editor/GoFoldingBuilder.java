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

package com.goide.editor;

import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.goide.psi.*;
import com.intellij.codeInsight.folding.CodeFoldingSettings;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.folding.NamedFoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class GoFoldingBuilder extends CustomFoldingBuilder implements DumbAware {
  private static void foldTypes(@Nullable PsiElement e, @NotNull List<FoldingDescriptor> result) {
    if (e instanceof GoStructType) {
      if (((GoStructType)e).getFieldDeclarationList().isEmpty()) return;
      fold(e, ((GoStructType)e).getLbrace(), ((GoStructType)e).getRbrace(), "{...}", result);
    }
    if (e instanceof GoInterfaceType) {
      if (e.getChildren().length == 0) return;
      fold(e, ((GoInterfaceType)e).getLbrace(), ((GoInterfaceType)e).getRbrace(), "{...}", result);
    }
  }

  private static void fold(@NotNull PsiElement e,
                           @Nullable PsiElement l,
                           @Nullable PsiElement r,
                           @NotNull String placeholderText,
                           @NotNull List<FoldingDescriptor> result) {
    if (l != null && r != null) {
      result.add(new NamedFoldingDescriptor(e, l.getTextRange().getStartOffset(), r.getTextRange().getEndOffset(), null, placeholderText));
    }
  }

  // com.intellij.codeInsight.folding.impl.JavaFoldingBuilderBase.addCodeBlockFolds()
  private static void addCommentFolds(@NotNull PsiElement comment,
                                      @NotNull Set<PsiElement> processedComments,
                                      @NotNull List<FoldingDescriptor> result) {
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
      int startOffset = comment.getTextRange().getStartOffset();
      int endOffset = end.getTextRange().getEndOffset();
      result.add(new NamedFoldingDescriptor(comment, startOffset, endOffset, null, "/.../"));
    }
  }

  @Override
  protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> result,
                                          @NotNull PsiElement root,
                                          @NotNull Document document,
                                          boolean quick) {
    if (!(root instanceof GoFile)) return;
    GoFile file = (GoFile)root;
    if (!file.isContentsLoaded()) return;

    GoImportList importList = ((GoFile)root).getImportList();
    if (importList != null) {
      GoImportDeclaration firstImport = ContainerUtil.getFirstItem(importList.getImportDeclarationList());
      if (firstImport != null) {
        PsiElement importKeyword = firstImport.getImport();
        int offset = importKeyword.getTextRange().getEndOffset();
        int startOffset = importKeyword.getNextSibling() instanceof PsiWhiteSpace ? offset + 1 : offset;
        int endOffset = importList.getTextRange().getEndOffset();
        if (endOffset - startOffset > 3) {
          result.add(new NamedFoldingDescriptor(importList, startOffset, endOffset, null, "..."));
        }
      }
    }

    for (GoBlock block : PsiTreeUtil.findChildrenOfType(file, GoBlock.class)) {
      if (block.getTextRange().getLength() > 1) {
        result.add(new NamedFoldingDescriptor(block.getNode(), block.getTextRange(), null, "{...}"));
      }
    }

    for (GoExprSwitchStatement switchStatement : PsiTreeUtil.findChildrenOfType(file, GoExprSwitchStatement.class)) {
      fold(switchStatement, switchStatement.getLbrace(), switchStatement.getRbrace(), "{...}", result);
    }

    for (GoSelectStatement selectStatement : PsiTreeUtil.findChildrenOfType(file, GoSelectStatement.class)) {
      fold(selectStatement, selectStatement.getLbrace(), selectStatement.getRbrace(), "{...}", result);
    }

    for (GoTypeSpec type : file.getTypes()) {
      foldTypes(type.getSpecType().getType(), result);
    }

    for (GoCaseClause caseClause : PsiTreeUtil.findChildrenOfType(file, GoCaseClause.class)) {
      PsiElement colon = caseClause.getColon();
      if (colon != null && !caseClause.getStatementList().isEmpty()) {
        fold(caseClause, colon.getNextSibling(), caseClause, "...", result);
      }
    }

    for (GoCommClause commClause : PsiTreeUtil.findChildrenOfType(file, GoCommClause.class)) {
      PsiElement colon = commClause.getColon();
      if (colon != null && !commClause.getStatementList().isEmpty()) {
        fold(commClause, colon.getNextSibling(), commClause, "...", result);
      }
    }

    for (GoVarDeclaration varDeclaration : PsiTreeUtil.findChildrenOfType(file, GoVarDeclaration.class)) {
      if (varDeclaration.getVarSpecList().size() > 1) {
        fold(varDeclaration, varDeclaration.getLparen(), varDeclaration.getRparen(), "(...)", result);
      }
    }

    for (GoConstDeclaration constDeclaration : PsiTreeUtil.findChildrenOfType(file, GoConstDeclaration.class)) {
      if (constDeclaration.getConstSpecList().size() > 1) {
        fold(constDeclaration, constDeclaration.getLparen(), constDeclaration.getRparen(), "(...)", result);
      }
    }

    for (GoTypeDeclaration typeDeclaration : PsiTreeUtil.findChildrenOfType(file, GoTypeDeclaration.class)) {
      if (typeDeclaration.getTypeSpecList().size() > 1) {
        fold(typeDeclaration, typeDeclaration.getLparen(), typeDeclaration.getRparen(), "(...)", result);
      }
    }

    for (GoCompositeLit compositeLit : PsiTreeUtil.findChildrenOfType(file, GoCompositeLit.class)) {
      GoLiteralValue literalValue = compositeLit.getLiteralValue();
      if (literalValue != null && literalValue.getElementList().size() > 1) {
        fold(literalValue, literalValue.getLbrace(), literalValue.getRbrace(), "{...}", result);
      }
    }

    if (!quick) {
      Set<PsiElement> processedComments = ContainerUtil.newHashSet();
      PsiTreeUtil.processElements(file, element -> {
        ASTNode node = element.getNode();
        IElementType type = node.getElementType();
        TextRange range = element.getTextRange();
        if (type == GoParserDefinition.MULTILINE_COMMENT && range.getLength() > 2) {
          result.add(new NamedFoldingDescriptor(node, range, null, "/*...*/"));
        }
        if (type == GoParserDefinition.LINE_COMMENT) {
          addCommentFolds(element, processedComments, result);
        }
        foldTypes(element, result); // folding for inner types
        return true;
      });
    }
  }

  @Nullable
  @Override
  protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
    return "...";
  }

  @Override
  protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
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
