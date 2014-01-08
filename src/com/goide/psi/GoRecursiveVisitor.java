package com.goide.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class GoRecursiveVisitor extends GoVisitor {
  @Override
  public void visitCompositeElement(@NotNull GoCompositeElement o) {
    for (PsiElement psiElement : o.getChildren()) {
      if (psiElement instanceof GoCompositeElement) {
        psiElement.accept(this);
      }
    }
  }

  @Override
  public void visitFile(PsiFile file) {
    for (PsiElement psiElement : file.getChildren()) {
      if (psiElement instanceof GoCompositeElement) {
        psiElement.accept(this);
      }
    }
  }
}