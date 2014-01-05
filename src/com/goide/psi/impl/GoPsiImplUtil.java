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

import java.util.List;

public class GoPsiImplUtil {
  private static final String MAIN_FUNCTION_NAME = "main";

  @Nullable
  public static GoTypeReferenceExpression getQualifier(@NotNull GoTypeReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoTypeReferenceExpression.class);
  }

  @Nullable
  public static PsiReference getReference(@NotNull GoTypeReferenceExpression o) {
    return new GoTypeReference(o);
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
  public static LookupElement createFunctionLookupElement(GoFunctionDeclaration f) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(f).withIcon(GoIcons.FUNCTION).withInsertHandler(null),
                                                 GoCompletionContributor.FUNCTION_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeLookupElement(GoTypeSpec t) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(t).withIcon(GoIcons.TYPE),
                                                 GoCompletionContributor.TYPE_PRIORITY);
  }

  @NotNull
  public static LookupElement createVariableLookupElement(GoVarDefinition v) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(v).withIcon(GoIcons.VARIABLE),
                                                 GoCompletionContributor.VAR_PRIORITY);
  }

  @Nullable
  public static GoFunctionDeclarationImpl findMainFunction(@NotNull GoFile file) {
    List<GoFunctionDeclaration> functions = file.getFunctions();
    for (GoFunctionDeclaration function : functions) {
      if (function instanceof GoFunctionDeclarationImpl && MAIN_FUNCTION_NAME.equals(function.getName())) {
        return (GoFunctionDeclarationImpl)function;
      }
    }
    return null;
  }
}
