package com.goide.completion;

import com.intellij.codeInsight.completion.CompletionConfidence;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;

public class GoCompletionConfidence extends CompletionConfidence {
  @NotNull
  @Override
  public ThreeState shouldSkipAutopopup(@NotNull PsiElement contextElement, @NotNull PsiFile psiFile, int offset) {
    return "_".equals(contextElement.getText()) ? ThreeState.YES : ThreeState.UNSURE;
  }
}
