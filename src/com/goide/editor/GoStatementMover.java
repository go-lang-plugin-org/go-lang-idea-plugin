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

import com.goide.psi.*;
import com.intellij.codeInsight.editorActions.moveUpDown.LineMover;
import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GoStatementMover extends LineMover {
  @Override
  public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo info, boolean down) {
    if (!(file instanceof GoFile && super.checkAvailable(editor, file, info, down))) return false;

    Couple<PsiElement> primeElementRange = getElementRange(editor, file);
    if (primeElementRange == null) return false;

    PsiElement commonParent = primeElementRange.first.isEquivalentTo(primeElementRange.second)
                              ? primeElementRange.first.getParent()
                              : PsiTreeUtil.findCommonParent(primeElementRange.first, primeElementRange.second);
    if (commonParent == null) return false;

    Couple<PsiElement> elementRange = getTopmostElementRange(primeElementRange, commonParent);
    if (elementRange == null) return false;

    if (commonParent == elementRange.first) commonParent = commonParent.getParent();
    info.toMove = new LineRange(elementRange.first, elementRange.second);
    
    if (elementRange.first instanceof GoTopLevelDeclaration && commonParent instanceof GoFile) {
      PsiElement toMove2 = getNeighborOfType(elementRange, GoTopLevelDeclaration.class, down);
      info.toMove2 = toMove2 != null ? new LineRange(toMove2) : null;
      return true;
    }
    if (commonParent instanceof GoImportList) {
      PsiElement toMove2 = getNeighborOfType(elementRange, GoImportDeclaration.class, down);
      info.toMove2 = toMove2 != null ? new LineRange(toMove2) : null;
      return true;
    }
    return setUpInfo(info, elementRange, commonParent, down);
  }

  private static Couple<PsiElement> getElementRange(@NotNull Editor editor, @NotNull PsiFile file) {
    Pair<PsiElement, PsiElement> primeElementRangePair = getElementRange(editor, file, getLineRangeFromSelection(editor));
    if (primeElementRangePair == null) return null;
    ASTNode firstNode = TreeUtil.findFirstLeaf(primeElementRangePair.first.getNode());
    ASTNode lastNode = TreeUtil.findLastLeaf(primeElementRangePair.second.getNode());
    if (firstNode == null || lastNode == null) return null;
    return Couple.of(firstNode.getPsi(), lastNode.getPsi());
  }

  /**
   * Return element range which contains TextRange(start, end) of top level elements
   * common parent of elements is straight parent for each element
   */
  @Nullable
  private static Couple<PsiElement> getTopmostElementRange(@NotNull Couple<PsiElement> elementRange, @NotNull PsiElement commonParent) {
    if (elementRange.first == null || elementRange.second == null) return null;
    int start = elementRange.first.getTextOffset();
    int end = elementRange.second.getTextRange().getEndOffset();

    TextRange range = commonParent.getTextRange();
    PsiElement[] children = commonParent.getChildren();
    if (commonParent.isEquivalentTo(elementRange.first) ||
        commonParent.isEquivalentTo(elementRange.second) ||
        range.getStartOffset() == start && (children.length == 0 || children[0].getTextRange().getStartOffset() > start) ||
        range.getEndOffset() == end && (children.length == 0 || children[children.length - 1].getTextRange().getEndOffset() < end)) {
      return Couple.of(commonParent, commonParent);
    }

    PsiElement startElement = elementRange.first;
    PsiElement endElement = elementRange.second;
    for (PsiElement element : children) {
      range = element.getTextRange();
      if (range.contains(start) && !range.contains(end)) {
        startElement = element;
      }
      if (range.contains(end - 1) && !range.contains(start - 1)) {
        endElement = element;
      }
    }

    return startElement.getParent().isEquivalentTo(endElement.getParent()) ? Couple.of(startElement, endElement) : null;
  }

  private static boolean setUpInfo(@NotNull MoveInfo info,
                                   @NotNull Couple<PsiElement> range,
                                   @NotNull PsiElement commonParent,
                                   boolean down) {
    info.toMove = new LineRange(range.first, range.second);
    info.toMove2 = null;
    if (range.first instanceof GoPackageClause) return true;
    PsiElement topLevelElement = PsiTreeUtil.findPrevParent(commonParent.getContainingFile(), commonParent);

    int nearLine = down ? info.toMove.endLine : info.toMove.startLine - 1;
    LineRange lineRange = new LineRange(topLevelElement);
    if (!lineRange.containsLine(down ? info.toMove.endLine + 1 : info.toMove.startLine - 2)) {
      return true;
    }

    info.toMove2 = lineRange.containsLine(down ? info.toMove.endLine + 1 : info.toMove.startLine - 2)
                   ? new LineRange(nearLine, nearLine + 1)
                   : null;
    return true;
  }

  @Nullable
  private static PsiElement getNeighborOfType(@NotNull Couple<PsiElement> range,
                                              @NotNull Class<? extends PsiElement> clazz,
                                              boolean rightNeighbor) {
    return rightNeighbor ? PsiTreeUtil.getNextSiblingOfType(range.second, clazz) : PsiTreeUtil.getPrevSiblingOfType(range.first, clazz);
  }
}

