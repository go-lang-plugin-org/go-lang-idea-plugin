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

package com.goide.actions.internal;

import com.goide.psi.GoExpression;
import com.goide.psi.GoFile;
import com.goide.psi.GoStatement;
import com.goide.psi.GoType;
import com.intellij.internal.SelectionBasedPsiElementInternalAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoShowTypeInternalAction extends SelectionBasedPsiElementInternalAction<GoExpression> {
  public GoShowTypeInternalAction() {
    super(GoExpression.class, GoFile.class);
  }

  @Nullable
  @Override
  protected String getInformationHint(@NotNull GoExpression element) {
    GoType type = element.getGoType();
    return type != null ? type.getText() : "<null>";
  }

  @NotNull
  @Override
  protected String getErrorHint() {
    return "Selection doesn't contain a Go expression";
  }

  @NotNull
  @Override
  protected List<GoExpression> getElementAtOffset(@NotNull Editor editor, @NotNull PsiFile file) {
    int offset = editor.getCaretModel().getOffset();
    PsiElement elementAtCaret = file.findElementAt(offset);
    if (!checkIntroduceContext(editor, elementAtCaret)) return ContainerUtil.emptyList();
    final List<GoExpression> expressions = ContainerUtil.newArrayList();
    while (elementAtCaret != null) {
      if (elementAtCaret instanceof GoStatement) break;
      if (elementAtCaret instanceof GoExpression) expressions.add((GoExpression)elementAtCaret);
      elementAtCaret = elementAtCaret.getParent();
    }
    return expressions;
  }
  
  private boolean checkIntroduceContext(@NotNull Editor editor, @Nullable PsiElement element) {
    if (PsiTreeUtil.getParentOfType(element, GoStatement.class) == null) {
      showError(editor);
      return false;
    }
    return true;
  }
}