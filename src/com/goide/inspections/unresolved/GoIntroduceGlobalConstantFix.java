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
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoIntroduceGlobalConstantFix extends GoUnresolvedFixBase {
  public GoIntroduceGlobalConstantFix(@NotNull PsiElement element, @NotNull String name) {
    super(element, name, "global constant");
  }

  @Override
  public void invoke(@NotNull Project project,
                     @NotNull PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement,
                     @NotNull PsiElement endElement) {
    GoTopLevelDeclaration declaration = GoPsiImplUtil.getTopLevelDeclaration(startElement);
    if (declaration == null || editor == null) return;
    Template template = TemplateSettings.getInstance().getTemplateById("go_lang_global_const_qf");
    if (template != null) {
      editor.getCaretModel().moveToOffset(declaration.getTextRange().getStartOffset());
      template.setToReformat(true);
      TemplateManager.getInstance(project).startTemplate(editor, template, true, ContainerUtil.stringMap("NAME", myName), null);
    }
  }
}