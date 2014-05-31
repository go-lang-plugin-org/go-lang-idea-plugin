package com.goide.inspections.unresolved;

import com.goide.psi.GoBlock;
import com.goide.psi.GoStatement;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoIntroduceLocalVariableFix extends GoUnresolvedFixBase {
  public GoIntroduceLocalVariableFix(@NotNull PsiElement element, @NotNull String name) {
    super(element, name, "local variable");
  }

  @Override
  public void invoke(@NotNull Project project,
                     @NotNull PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement,
                     @NotNull PsiElement endElement) {
    GoStatement statement = PsiTreeUtil.getParentOfType(startElement, GoStatement.class);
    while (statement != null && !(statement.getParent() instanceof GoBlock)) {
      statement = PsiTreeUtil.getParentOfType(statement, GoStatement.class);
    }
    if (statement == null || editor == null) return;
    TemplateManagerImpl templateManager = (TemplateManagerImpl)TemplateManager.getInstance(project);
    Template template = TemplateSettings.getInstance().getTemplateById("go_lang_local_var_qf");
    if (template != null) {
      int start = statement.getTextRange().getStartOffset();
      editor.getCaretModel().moveToOffset(start);
      template.setToReformat(true);
      templateManager.startTemplate(editor, template, true, ContainerUtil.stringMap("NAME", myName), null);
    }
  }
}
