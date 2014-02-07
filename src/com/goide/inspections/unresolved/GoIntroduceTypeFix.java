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

public class GoIntroduceTypeFix extends GoUnresolvedFixBase {
  public GoIntroduceTypeFix(@NotNull PsiElement element, String name) {
    super(element, name, "type");
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
    Template template = TemplateSettings.getInstance().getTemplateById("go_lang_type_qf");
    if (template != null) {
      int start = decl.getTextRange().getStartOffset();
      editor.getDocument().insertString(start, "\n\n");
      editor.getCaretModel().moveToOffset(start);
      templateManager.startTemplate(editor, template, true, ContainerUtil.stringMap("NAME", myName), null);
    }
  }
}
