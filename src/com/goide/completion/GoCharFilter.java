package com.goide.completion;

import com.goide.psi.GoImportString;
import com.intellij.codeInsight.lookup.CharFilter;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public class GoCharFilter extends CharFilter {
  @Nullable
  @Override
  public Result acceptChar(char c, int prefixLength, Lookup lookup) {
    if (lookup.isCompletion() && '/' == c && PsiTreeUtil.getParentOfType(lookup.getPsiElement(), GoImportString.class) != null) {
      return Result.ADD_TO_PREFIX;
    }
    return null;
  }
}
