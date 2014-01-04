package com.goide.psi.impl;

import com.goide.GoIcons;
import com.goide.completion.GoCompletionContributor;
import com.goide.psi.*;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoPsiImplUtil {
  @Nullable
  public static PsiReference getReference(@NotNull GoTypeReferenceExpression o) {
    return null;
  }

  @Nullable
  public static GoReferenceExpression getQualifier(@NotNull GoReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoReferenceExpression.class);
  }

  @Nullable
  public static PsiReference getReference(@NotNull final GoReferenceExpression o) {
    return new GoReference(o);
  }

  @NotNull
  static LookupElement createFunctionLookupElement(GoFunctionDeclaration f) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(f).withIcon(GoIcons.FUNCTION).withInsertHandler(null),
                                                 GoCompletionContributor.FUNCTION_PRIORITY);
  }
}
