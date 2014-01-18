package com.goide.psi.impl;

import com.goide.GoIcons;
import com.goide.completion.GoCompletionContributor;
import com.goide.psi.*;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class GoPsiImplUtil {
  @Nullable
  public static GoTypeReferenceExpression getQualifier(@NotNull GoTypeReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoTypeReferenceExpression.class);
  }

  @Nullable
  public static PsiReference getReference(@NotNull GoTypeReferenceExpression o) {
    return new GoTypeReference(o);
  }

  @Nullable
  public static PsiReference getReference(@NotNull GoImportString o) {
    if (o.getTextLength() < 2) return null;
    return new GoImportReference(o, TextRange.from(1, o.getTextLength() - 2));
  }

  @Nullable
  public static GoReferenceExpression getQualifier(@NotNull GoReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoReferenceExpression.class);
  }

  @Nullable
  public static PsiReference getReference(@NotNull final GoReferenceExpression o) {
    return new GoReference(o);
  }

  @SuppressWarnings("UnusedParameters")
  public static boolean processDeclarations(@NotNull GoCompositeElement o,
                                            @NotNull PsiScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            PsiElement lastParent,
                                            @NotNull PsiElement place) {
    boolean isAncestor = PsiTreeUtil.isAncestor(o, place, false);
    if (isAncestor) return GoCompositeElementImpl.precessDeclarationDefault(o, processor, state, lastParent, place);

    if (o instanceof GoBlock ||
        o instanceof GoIfStatement ||
        o instanceof GoSwitchStatement ||
        o instanceof GoForStatement ||
        o instanceof GoCommClause ||
        o instanceof GoTypeCaseClause ||
        o instanceof GoExprCaseClause) {
      return processor.execute(o, state);
    }
    return GoCompositeElementImpl.precessDeclarationDefault(o, processor, state, lastParent, place);
  }

  @NotNull
  public static LookupElement createFunctionLookupElement(@NotNull GoFunctionDeclaration f) {
    Icon icon = f instanceof GoMethodDeclaration ? GoIcons.METHOD : GoIcons.FUNCTION;
    GoSignature signature = f.getSignature();
    int paramsCount = 0;
    if (signature != null) {
      paramsCount = signature.getParameters().getParameterDeclarationList().size();
    }
    InsertHandler<LookupElement> handler = paramsCount == 0 ? ParenthesesInsertHandler.NO_PARAMETERS : ParenthesesInsertHandler.WITH_PARAMETERS;
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(f).withIcon(icon).withInsertHandler(handler),
                                                 GoCompletionContributor.FUNCTION_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeLookupElement(@NotNull GoTypeSpec t) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(t).withIcon(GoIcons.TYPE),
                                                 GoCompletionContributor.TYPE_PRIORITY);
  }

  @NotNull
  public static LookupElement createVariableLikeLookupElement(@NotNull GoNamedElement v) {
    Icon icon = v instanceof GoVarDefinition ? GoIcons.VARIABLE :
                v instanceof GoParamDefinition ? GoIcons.PARAMETER :
                v instanceof GoFieldDefinition ? GoIcons.FIELD :
                v instanceof GoReceiver ? GoIcons.RECEIVER :
                v instanceof GoConstDefinition ? GoIcons.CONST :
                null;
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(v).withIcon(icon), GoCompletionContributor.VAR_PRIORITY);
  }

  @NotNull
  public static LookupElement createImportLookupElement(@NotNull String i) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(i).withIcon(GoIcons.PACKAGE), GoCompletionContributor.PACKAGE_PRIORITY);
  }

  @Nullable
  public static GoType getGoType(@NotNull GoReceiver o) {
    return o.getType();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoExpression o) {
    if (o instanceof GoUnaryTypeCreateExpr) {
      GoTypeReferenceExpression expression = ((GoUnaryTypeCreateExpr)o).getTypeReferenceExpression();
      PsiReference reference = expression.getReference();
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (resolve instanceof GoTypeSpec) return ((GoTypeSpec)resolve).getType();
    }
    return null;
  }

  @Nullable
  public static GoType getGoType(@NotNull GoVarDefinition o) {
    PsiElement parent = o.getParent();
    if (parent instanceof GoShortVarDeclaration) {
      List<GoVarDefinition> defList = ((GoShortVarDeclaration)parent).getVarDefinitionList();
      int i = 0;
      for (GoVarDefinition d : defList) {
        if (d.equals(o)) break;
        i++;
      }
      List<GoExpression> exprs = ((GoShortVarDeclaration)parent).getExpressionList();
      if (exprs.size() < i) return null;
      return exprs.get(i).getGoType();
    }
    return GoNamedElementImpl.getType(o);
  }
}
