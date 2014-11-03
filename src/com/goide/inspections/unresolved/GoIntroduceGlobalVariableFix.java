/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.inspections.unresolved;

import com.goide.psi.GoTopLevelDeclaration;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoIntroduceGlobalVariableFix extends GoUnresolvedFixBase {
  public GoIntroduceGlobalVariableFix(@NotNull PsiElement element, @NotNull String name) {
    super(element, name, "global variable");
  }

  @Override
  public void invoke(@NotNull Project project,
                     @NotNull PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement,
                     @NotNull PsiElement endElement) {
    GoTopLevelDeclaration decl = getTopLevelDeclaration(startElement);
    if (decl == null || editor == null) return;
    TemplateManagerImpl templateManager = (TemplateManagerImpl)TemplateManager.getInstance(project);
    Template template = TemplateSettings.getInstance().getTemplateById("go_lang_global_var_qf");
    if (template != null) {
      int start = decl.getTextRange().getStartOffset();
      editor.getCaretModel().moveToOffset(start);
      template.setToReformat(true);
      templateManager.startTemplate(editor, template, true, ContainerUtil.stringMap("NAME", myName), null);
    }
  }
}
