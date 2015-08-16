/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.psi.impl.imports;

import com.goide.GoLanguage;
import com.goide.codeInsight.imports.GoCodeInsightSettings;
import com.goide.codeInsight.imports.GoImportPackageQuickFix;
import com.goide.psi.GoCompositeElement;
import com.goide.psi.impl.GoReference;
import com.intellij.codeInsight.daemon.ReferenceImporter;
import com.intellij.codeInsight.daemon.impl.CollectHighlightsUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoReferenceImporter implements ReferenceImporter {
  @Override
  public boolean autoImportReferenceAtCursor(@NotNull Editor editor, @NotNull PsiFile file) {
    if (!file.getViewProvider().getLanguages().contains(GoLanguage.INSTANCE)) {
      return false;
    }

    int caretOffset = editor.getCaretModel().getOffset();
    Document document = editor.getDocument();
    int lineNumber = document.getLineNumber(caretOffset);
    int startOffset = document.getLineStartOffset(lineNumber);
    int endOffset = document.getLineEndOffset(lineNumber);

    List<PsiElement> elements = CollectHighlightsUtil.getElementsInRange(file, startOffset, endOffset);
    for (PsiElement element : elements) {
      if (element instanceof GoCompositeElement) {
        for (PsiReference reference : element.getReferences()) {
          if (reference instanceof GoReference) {
            GoImportPackageQuickFix fix = new GoImportPackageQuickFix(reference);
            if (fix.isAvailable(file.getProject(), editor, file)) {
              fix.applyFix();
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  @Override
  public boolean autoImportReferenceAt(@NotNull final Editor editor, @NotNull final PsiFile file, int offset) {
    if (!file.getViewProvider().getLanguages().contains(GoLanguage.INSTANCE) ||
        !GoCodeInsightSettings.getInstance().isAddUnambiguousImportsOnTheFly()) {
      return false;
    }

    final PsiReference reference = file.findReferenceAt(offset);
    if (reference instanceof GoReference) {
      final GoImportPackageQuickFix fix = new GoImportPackageQuickFix(reference);
      final Project project = file.getProject();
      if (fix.isAvailable(project, editor, file)) {
        fix.applyFix();
        return true;
      }
    }
    return false;
  }
}
