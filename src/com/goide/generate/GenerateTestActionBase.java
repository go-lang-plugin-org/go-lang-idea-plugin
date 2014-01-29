package com.goide.generate;

import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.CodeInsightAction;
import com.intellij.codeInsight.generation.actions.GenerateActionPopupTemplateInjector;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class GenerateTestActionBase extends CodeInsightAction implements GenerateActionPopupTemplateInjector {
  private final GenerateTestHandler myHandler;

  public GenerateTestActionBase(GenerateTestHandler.Type functionType) {
    myHandler = new GenerateTestHandler(functionType);
  }

  @NotNull
  @Override
  protected CodeInsightActionHandler getHandler() {
    return myHandler;
  }

  @Override
  protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
    return GoTestFinder.isTestFile(file);
  }

  @Nullable
  @Override
  public AnAction createEditTemplateAction(DataContext dataContext) {
    return null;
  }

  public static class GenerateTestHandler implements CodeInsightActionHandler {
    private final Type myType;

    public enum Type {TEST, BENCHMARK}

    public GenerateTestHandler(Type type) {
      myType = type;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
      if (!CommonRefactoringUtil.checkReadOnlyStatus(file)) return;

      TemplateManagerImpl templateManager = (TemplateManagerImpl)TemplateManager.getInstance(project);
      Template template = TemplateSettings.getInstance().getTemplateById("go_lang_" + myType.name().toLowerCase());
      if (template != null) {
        templateManager.startTemplate(editor, template);
      }
      else {
        HintManager.getInstance().showErrorHint(editor, "No template found for generator");
      }
    }

    @Override
    public boolean startInWriteAction() {
      return true;
    }
  }
}
