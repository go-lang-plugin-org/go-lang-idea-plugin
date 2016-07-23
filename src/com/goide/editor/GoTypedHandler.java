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

import com.intellij.codeInsight.template.impl.editorActions.TypedActionHandlerBase;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

public class GoTypedHandler extends TypedActionHandlerBase {
  public GoTypedHandler(TypedActionHandler originalHandler) {
    super(originalHandler);
  }

  @Override
  public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
    if (myOriginalHandler != null) myOriginalHandler.execute(editor, c, dataContext);
    if (c != 'e') return;
    Project project = editor.getProject();
    if (project == null) return;
    int offset = editor.getCaretModel().getOffset();
    if (offset < 4) return;
    TextRange from = TextRange.from(offset - 4, 4);
    String text = editor.getDocument().getText(from);
    if ("case".equals(text)) {
      PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
      ApplicationManager.getApplication().runWriteAction(() -> {
        if (project.isDisposed()) return;
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) return;
        CodeStyleManager.getInstance(project).adjustLineIndent(file, from);
      });
    }
  }
}
