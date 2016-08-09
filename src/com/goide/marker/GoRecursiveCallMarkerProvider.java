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

package com.goide.marker;

import com.goide.psi.GoCallExpr;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.FunctionUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GoRecursiveCallMarkerProvider implements LineMarkerProvider {
  @Override
  public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
    return null;
  }

  @Override
  public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    Set<Integer> lines = ContainerUtil.newHashSet();
    for (PsiElement element : elements) {
      if (element instanceof GoCallExpr) {
        PsiElement resolve = GoPsiImplUtil.resolveCall((GoCallExpr)element);
        if (resolve instanceof GoFunctionOrMethodDeclaration) {
          if (isRecursiveCall(element, (GoFunctionOrMethodDeclaration)resolve)) {
            PsiDocumentManager instance = PsiDocumentManager.getInstance(element.getProject());
            Document document = instance.getDocument(element.getContainingFile());
            int textOffset = element.getTextOffset();
            if (document == null) continue;
            int lineNumber = document.getLineNumber(textOffset);
            if (!lines.contains(lineNumber)) {
              result.add(new RecursiveMethodCallMarkerInfo(element));
            }
            lines.add(lineNumber);
          }
        }
      }
    }
  }

  private static boolean isRecursiveCall(PsiElement element, GoFunctionOrMethodDeclaration function) {
    return Comparing.equal(PsiTreeUtil.getParentOfType(element, GoFunctionOrMethodDeclaration.class), function);
  }

  private static class RecursiveMethodCallMarkerInfo extends LineMarkerInfo<PsiElement> {
    private RecursiveMethodCallMarkerInfo(@NotNull PsiElement methodCall) {
      super(methodCall,
            methodCall.getTextRange(),
            AllIcons.Gutter.RecursiveMethod,
            Pass.UPDATE_OVERRIDDEN_MARKERS,
            FunctionUtil.constant("Recursive call"),
            null,
            GutterIconRenderer.Alignment.RIGHT
      );
    }
  }
}


