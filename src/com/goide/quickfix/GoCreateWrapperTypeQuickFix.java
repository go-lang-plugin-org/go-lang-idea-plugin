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

package com.goide.quickfix;

import com.goide.psi.GoType;
import com.goide.psi.GoTypeDeclaration;
import com.goide.psi.GoTypeSpec;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateBuilderImpl;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.diagnostic.AttachmentFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoCreateWrapperTypeQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

  private static final String INPUT_NAME = "INPUTVAR";
  private static final String OTHER_NAME = "OTHERVAR";

  public static final String QUICKFIX_NAME = "Create type";

  public GoCreateWrapperTypeQuickFix(@NotNull GoType type) {
    super(type);
  }

  @Override
  public void invoke(@NotNull Project project,
                     @NotNull PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement,
                     @NotNull PsiElement endElement) {
    if (editor == null) {
      LOG.error("Cannot run quick fix without editor: " + getClass().getSimpleName(),
                AttachmentFactory.createAttachment(file.getVirtualFile()));
      return;
    }
    if (!(startElement instanceof GoType)) return;
    GoType type = (GoType)startElement;

    PsiElement anchor = PsiTreeUtil.findPrevParent(file, type);

    String name = "TypeName";
    GoTypeDeclaration decl = (GoTypeDeclaration)file.addBefore(GoElementFactory.createTypeDeclaration(project, name, type), anchor);
    if (decl == null) return;
    decl = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(decl);
    if (decl == null) return;
    GoTypeSpec spec = ContainerUtil.getFirstItem(decl.getTypeSpecList());
    if (spec == null) return;

    TemplateBuilderImpl builder = new TemplateBuilderImpl(file);
    builder.replaceElement(type, OTHER_NAME, INPUT_NAME, false);
    builder.replaceElement(spec.getIdentifier(), INPUT_NAME, new ConstantNode(name), true);

    editor.getCaretModel().moveToOffset(file.getTextRange().getStartOffset());
    Template template = builder.buildInlineTemplate();
    editor.getCaretModel().moveToOffset(file.getTextRange().getStartOffset());
    TemplateManager.getInstance(project).startTemplate(editor, template);
  }

  @NotNull
  @Override
  public String getText() {
    return QUICKFIX_NAME;
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return QUICKFIX_NAME;
  }
}
