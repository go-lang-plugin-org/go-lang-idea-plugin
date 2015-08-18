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

package com.goide.editor.marker;

import com.goide.psi.GoFile;
import com.goide.psi.GoTopLevelDeclaration;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.impl.LineMarkersPass;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class GoMethodSeparatorProvider implements LineMarkerProvider {
  private final DaemonCodeAnalyzerSettings myDaemonSettings;
  private final EditorColorsManager myColorsManager;

  public GoMethodSeparatorProvider(DaemonCodeAnalyzerSettings daemonSettings, EditorColorsManager colorsManager) {
    myDaemonSettings = daemonSettings;
    myColorsManager = colorsManager;
  }

  @Nullable
  @Override
  public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement o) {
    if (myDaemonSettings.SHOW_METHOD_SEPARATORS && o instanceof GoTopLevelDeclaration && o.getParent() instanceof GoFile) {
      return LineMarkersPass.createMethodSeparatorLineMarker(findAnchorElement((GoTopLevelDeclaration)o), myColorsManager);
    }
    return null;
  }

  @Override
  public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
  }

  @NotNull
  private static PsiElement findAnchorElement(@NotNull GoTopLevelDeclaration o) {
    PsiElement result = o;
    PsiElement p = o;
    while ((p = p.getPrevSibling()) != null) {
      if (p instanceof PsiComment) {
        result = p;
      }
      else if (p instanceof PsiWhiteSpace) {
        if (p.getText().contains("\n\n")) return result;
      }
      else {
        break;
      }
    }
    return result;
  }
}