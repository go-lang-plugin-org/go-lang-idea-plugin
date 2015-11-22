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

package com.goide.highlighting;

import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactoryBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoHighlightExitPointsHandlerFactory extends HighlightUsagesHandlerFactoryBase {
  @Nullable
  @Override
  public HighlightUsagesHandlerBase createHighlightUsagesHandler(@NotNull Editor editor, @NotNull PsiFile file, @NotNull PsiElement target) {
    return MyHandler.createForElement(editor, file, target);
  }

  public static class MyHandler extends HighlightUsagesHandlerBase<PsiElement> {
    private final PsiElement myTarget;
    private final GoTypeOwner myFunction;

    private MyHandler(Editor editor, PsiFile file, PsiElement target, GoTypeOwner function) {
      super(editor, file);
      myTarget = target;
      myFunction = function;
    }

    @NotNull
    @Override
    public List<PsiElement> getTargets() {
      return ContainerUtil.newSmartList(myTarget);
    }

    @Override
    protected void selectTargets(List<PsiElement> targets, @NotNull Consumer<List<PsiElement>> selectionConsumer) {
      selectionConsumer.consume(targets);
    }

    @Override
    public void computeUsages(List<PsiElement> targets) {
      if (myTarget instanceof LeafPsiElement && ((LeafPsiElement)myTarget).getElementType() == GoTypes.FUNC) {
        addOccurrence(myTarget);
      }
      new GoRecursiveVisitor() {
        @Override
        public void visitFunctionLit(@NotNull GoFunctionLit literal) {
        }

        @Override
        public void visitReturnStatement(@NotNull GoReturnStatement statement) {
          addOccurrence(statement);
        }

        @Override
        public void visitCallExpr(@NotNull GoCallExpr o) {
          if (GoPsiImplUtil.isPanic(o)) addOccurrence(o);
          super.visitCallExpr(o);
        }
      }.visitTypeOwner(myFunction);
    }

    @Nullable
    public static MyHandler createForElement(@NotNull Editor editor, PsiFile file, PsiElement element) {
      GoTypeOwner function = PsiTreeUtil.getParentOfType(element, GoFunctionLit.class, GoFunctionOrMethodDeclaration.class);
      if (function == null) return null;
      if (shouldCreateMyHandler(element)) {
        return new MyHandler(editor, file, element, function);
      }
      return null;
    }

    private static boolean shouldCreateMyHandler(PsiElement element) {
      if (element instanceof LeafPsiElement) {
        LeafPsiElement leaf = (LeafPsiElement)element;
        return leaf.getElementType() == GoTypes.RETURN || leaf.getElementType() == GoTypes.FUNC || isPanicCall(leaf);
      }
      else {
        return false;
      }
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
}