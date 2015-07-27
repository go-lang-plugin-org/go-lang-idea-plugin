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

package com.goide.refactor;

import com.goide.psi.GoExpression;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoIntroduceVariableHandler extends GoIntroduceVariableBase implements RefactoringActionHandler {
  @Override
  public void invoke(@NotNull final Project project, final Editor editor, PsiFile file, DataContext dataContext) {
    if (!CommonRefactoringUtil.checkReadOnlyStatus(file)) return;
    if (editor.getSettings().isVariableInplaceRenameEnabled()) {
      final TemplateState templateState = TemplateManagerImpl.getTemplateState(editor);
      if (templateState != null && !templateState.isFinished()) return;
    }
    SelectionModel selectionModel = editor.getSelectionModel();
    List<GoExpression> expressions = selectionModel.hasSelection()
                                     ? collectExpressionsInSelection(file, selectionModel)
                                     : collectExpressionsAtOffset(file, editor.getDocument(), editor.getCaretModel().getOffset());
    if (expressions.size() > 1) {
      smartIntroduce(project, editor, expressions);
    }
    else if (expressions.size() == 1) {
      GoExpression expression = ContainerUtil.getFirstItem(expressions);
      assert expression != null;
      performOnElement(project, editor, expression);
    }
    else {
      showCannotPerform(project, editor);
    }
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
  }
}
