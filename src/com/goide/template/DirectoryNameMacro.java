package com.goide.template;

import com.intellij.codeInsight.template.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectoryNameMacro extends Macro {

  @Override
  public String getName() {
    return "directoryName";
  }

  @Override
  public String getPresentableName() {
    return "directoryName";
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
    PsiElement psiEle = context.getPsiElementAtStartOffset();
    if (psiEle != null) {
      VirtualFile file = psiEle.getContainingFile().getVirtualFile();
      if (file != null) {
        VirtualFile parent = file.getParent();
        if (parent != null) {
          return calculateResult(parent);
        }
      }
    }
    return null;
  }

  @Nullable
  protected TextResult calculateResult(@NotNull VirtualFile virtualFile) {
    return new TextResult(virtualFile.getName());
  }
}
