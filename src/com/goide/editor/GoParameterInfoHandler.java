package com.goide.editor;

import com.goide.GoTypes;
import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class GoParameterInfoHandler implements ParameterInfoHandlerWithTabActionSupport<GoArgumentList, Object, GoExpression>, DumbAware {
  @NotNull
  @Override
  public GoExpression[] getActualParameters(@NotNull GoArgumentList o) {
    return ArrayUtil.toObjectArray(o.getExpressionList(), GoExpression.class);
  }

  @NotNull
  @Override
  public IElementType getActualParameterDelimiterType() {
    return GoTypes.COMMA;
  }

  @NotNull
  @Override
  public IElementType getActualParametersRBraceType() {
    return GoTypes.RPAREN;
  }

  @NotNull
  @Override
  public Set<Class> getArgumentListAllowedParentClasses() {
    return ContainerUtil.newHashSet();
  }

  @NotNull
  @Override
  public Set<? extends Class> getArgListStopSearchClasses() {
    return ContainerUtil.newHashSet();
  }

  @NotNull
  @Override
  public Class<GoArgumentList> getArgumentListClass() {
    return GoArgumentList.class;
  }

  @Override
  public boolean couldShowInLookup() {
    return true;
  }

  @Nullable
  @Override
  public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  @Nullable
  @Override
  public Object[] getParametersForDocumentation(Object p, ParameterInfoContext context) {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  @Nullable
  @Override
  public GoArgumentList findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
    // todo: see ParameterInfoUtils.findArgumentList
    return getList(context);
  }

  @Nullable
  private static GoArgumentList getList(@NotNull ParameterInfoContext context) {
    PsiElement at = context.getFile().findElementAt(context.getOffset());
    return PsiTreeUtil.getParentOfType(at, GoArgumentList.class);
  }

  @Override
  public void showParameterInfo(@NotNull GoArgumentList argList, @NotNull CreateParameterInfoContext context) {
    PsiElement parent = argList.getParent();
    if (!(parent instanceof GoCallExpr)) return;

    GoExpression expression = ((GoCallExpr)parent).getExpression();
    if (expression instanceof GoReferenceExpression) {
      PsiReference reference = expression.getReference();
      PsiElement resolve = reference != null ? reference.resolve() : null;

      if (resolve instanceof GoSignatureOwner) {
        context.setItemsToShow(new Object[]{resolve});
        context.showHint(argList, argList.getTextRange().getStartOffset(), this);
      }
    }
  }

  @Nullable
  @Override
  public GoArgumentList findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
    return getList(context);
  }

  @Override
  public void updateParameterInfo(@NotNull GoArgumentList list, @NotNull UpdateParameterInfoContext context) {
    context.setCurrentParameter(ParameterInfoUtils.getCurrentParameterIndex(list.getNode(), context.getOffset(), GoTypes.COMMA));
  }

  @Nullable
  @Override
  public String getParameterCloseChars() {
    return ",(";
  }

  @Override
  public boolean tracksParameterIndex() {
    return true;
  }

  @Override
  public void updateUI(@Nullable Object p, @NotNull ParameterInfoUIContext context) {
    if (p == null) {
      context.setUIComponentEnabled(false);
      return;
    }

    StringBuilder builder = new StringBuilder();

    int start = 0;
    int end = 0;

    int index = context.getCurrentParameterIndex();

    if (p instanceof GoSignatureOwner) {
      GoSignature signature = ((GoSignatureOwner)p).getSignature();
      if (signature != null) {
        List<GoParameterDeclaration> list = signature.getParameters().getParameterDeclarationList();
        for (int i = 0; i < list.size(); i++) {
          if (i != 0) builder.append(", ");
          if (index == i) start = builder.length();
          builder.append(list.get(i).getText());

          if (index == i) end = builder.length();
        }
      }
    }
    context.setupUIComponentPresentation(builder.toString(), start, end, false, false, false, context.getDefaultParameterColor());
  }
}
