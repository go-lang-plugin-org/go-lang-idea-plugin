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