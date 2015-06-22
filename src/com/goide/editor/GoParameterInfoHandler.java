/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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
import com.goide.psi.impl.GoPsiImplUtil;
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

    GoCallExpr call = (GoCallExpr)parent;
    PsiReference ref = GoPsiImplUtil.getCallReference(call);
    PsiElement resolve = ref != null ? ref.resolve() : null;
    if (ref == null && ((call).getExpression() instanceof GoFunctionLit)) {
      context.setItemsToShow(new Object[]{((GoFunctionLit)(call).getExpression())});
      context.showHint(argList, argList.getTextRange().getStartOffset(), this);
    }
    else if (resolve instanceof GoSignatureOwner) {
      context.setItemsToShow(new Object[]{resolve});
      context.showHint(argList, argList.getTextRange().getStartOffset(), this);
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
    GoSignature signature = p instanceof GoSignatureOwner ? ((GoSignatureOwner)p).getSignature() : null;
    if (signature == null) return null;
    GoParameters parameters = signature.getParameters();
    List<String> parametersPresentations = getParameterPresentations(parameters);
    // Figure out what particular presentation is actually selected. Take in
    // account possibility of the last variadic parameter.
    int selected = isLastParameterVariadic(parameters.getParameterDeclarationList())
                   ? Math.min(context.getCurrentParameterIndex(), parametersPresentations.size() - 1)
                   : context.getCurrentParameterIndex();
    // Build the parameter presentation string.
    StringBuilder builder = new StringBuilder();
    int start = 0;
    int end = 0;
    for (int i = 0; i < parametersPresentations.size(); ++i) {
      if (i != 0) {
        builder.append(", ");
      }
      if (i == selected) {
        start = builder.length();
      }
      builder.append(parametersPresentations.get(i));

      if (i == selected) {
        end = builder.length();
      }
    }
    return context.setupUIComponentPresentation(builder.toString(), start, end, false, false, false, context.getDefaultParameterColor());
  }

  /**
   * Creates a list of parameter presentations. For clarity we expand parameters declared as `a, b, c int` into `a int, b int, c int`.
   */
  @NotNull
  public static List<String> getParameterPresentations(@NotNull GoParameters parameters) {
    List<GoParameterDeclaration> paramDeclarations = parameters.getParameterDeclarationList();
    List<String> paramPresentations = ContainerUtil.newArrayListWithCapacity(2 * paramDeclarations.size());
    for (GoParameterDeclaration paramDeclaration : paramDeclarations) {
      boolean isVariadic = paramDeclaration.isVariadic();
      final List<GoParamDefinition> paramDefinitionList = paramDeclaration.getParamDefinitionList();
      for (GoParamDefinition paramDefinition : paramDefinitionList) {
        String separator = isVariadic ? " ..." : " ";
        paramPresentations.add(paramDefinition.getText() + separator + paramDeclaration.getType().getText());
      }
      if (paramDefinitionList.isEmpty()) {
        String separator = isVariadic ? "..." : "";
        paramPresentations.add(separator + paramDeclaration.getType().getText());
      }
    }
    return paramPresentations;
  }

  private static boolean isLastParameterVariadic(@NotNull List<GoParameterDeclaration> declarations) {
    GoParameterDeclaration lastItem = ContainerUtil.getLastItem(declarations);
    return lastItem != null && lastItem.isVariadic();
  }
}
