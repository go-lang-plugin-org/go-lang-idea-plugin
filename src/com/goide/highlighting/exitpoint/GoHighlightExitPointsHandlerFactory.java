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

package com.goide.highlighting.exitpoint;

import com.goide.GoTypes;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoReferenceExpression;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactoryBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoHighlightExitPointsHandlerFactory extends HighlightUsagesHandlerFactoryBase {
  private static final TokenSet BREAK_HIGHLIGHTING_TOKENS = TokenSet.create(GoTypes.BREAK, GoTypes.SWITCH, GoTypes.FOR, GoTypes.SELECT);

  @Nullable
  @Override
  public HighlightUsagesHandlerBase createHighlightUsagesHandler(@NotNull Editor editor,
                                                                 @NotNull PsiFile file,
                                                                 @NotNull PsiElement target) {
    if (target instanceof LeafPsiElement) {
      IElementType elementType = ((LeafPsiElement)target).getElementType();
      if (elementType == GoTypes.RETURN || elementType == GoTypes.FUNC || isPanicCall(target)) {
        return GoFunctionExitPointHandler.createForElement(editor, file, target);
      }
      else if (BREAK_HIGHLIGHTING_TOKENS.contains(elementType)) {
        return GoBreakStatementExitPointHandler.createForElement(editor, file, target);
      }
    }
    return null;
  }

  private static boolean isPanicCall(@NotNull PsiElement e) {
    PsiElement parent = e.getParent();
    if (parent instanceof GoReferenceExpression) {
      PsiElement grandPa = parent.getParent();
      if (grandPa instanceof GoCallExpr) return GoPsiImplUtil.isPanic((GoCallExpr)grandPa);
    }
    return false;
  }
}