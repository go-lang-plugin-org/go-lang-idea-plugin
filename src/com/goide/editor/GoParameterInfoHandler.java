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

package com.goide.editor;

import com.goide.GoTypes;
import com.goide.psi.*;
import com.google.common.collect.Lists;
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
    updatePresentation(p, context);
  }

  String updatePresentation(@Nullable Object p, @NotNull ParameterInfoUIContext context) {
    if (p == null) {
      context.setUIComponentEnabled(false);
      return null;
    }
    if (!(p instanceof GoSignatureOwner)) {
      return null;
    }
    final GoSignature signature = ((GoSignatureOwner)p).getSignature();
    if (signature == null) {
      return null;
    }
    // Create a list of parameter presentations. For clarity we expand
    // parameters declared as `a, b, c in` into `a int, b int, c int`.
    final List<GoParameterDeclaration> paramDeclarations = signature.getParameters().getParameterDeclarationList();
    final List<String> paramPresentations = Lists.newArrayListWithExpectedSize(2 * paramDeclarations.size());
    boolean isVariadic = false;
    for (GoParameterDeclaration paramDeclaration : paramDeclarations) {
      isVariadic = paramDeclaration.isVariadic();
      for (GoParamDefinition paramDefinition : paramDeclaration.getParamDefinitionList()) {
        final String separator = isVariadic ? " ..." : " ";
        paramPresentations.add(paramDefinition.getText() + separator + paramDeclaration.getType().getText());
      }
    }
    // Figure out what particular presentation is actually selected. Take in
    // account possibility of the last variadic parameter.
    final int selected;
    if (isVariadic) {
      selected = Math.min(context.getCurrentParameterIndex(), paramPresentations.size() - 1);
    }
    else {
      selected = context.getCurrentParameterIndex();
    }
    // Build the parameter presentation string.
    final StringBuilder builder = new StringBuilder();
    int start = 0;
    int end = 0;
    for (int i = 0; i < paramPresentations.size(); ++i) {
      if (i != 0) {
        builder.append(", ");
      }
      if (i == selected) {
        start = builder.length();
      }
      builder.append(paramPresentations.get(i));

      if (i == selected) {
        end = builder.length();
      }
    }
    return context.setupUIComponentPresentation(builder.toString(), start, end, false, false, false, context.getDefaultParameterColor());
  }
}
