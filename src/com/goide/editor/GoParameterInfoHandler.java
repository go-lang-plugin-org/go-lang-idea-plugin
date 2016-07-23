/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class GoParameterInfoHandler implements ParameterInfoHandlerWithTabActionSupport<GoArgumentList, Object, GoExpression> {
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
  public Set<Class> getArgListStopSearchClasses() {
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
    GoFunctionType type = findFunctionType(((GoCallExpr)parent).getExpression().getGoType(null));
    if (type != null) {
      context.setItemsToShow(new Object[]{type});
      context.showHint(argList, argList.getTextRange().getStartOffset(), this);
    }
  }

  @Nullable
  private static GoFunctionType findFunctionType(@Nullable GoType type) {
    if (type instanceof GoFunctionType || type == null) return (GoFunctionType)type;
    GoType base = type.getUnderlyingType();
    return base instanceof GoFunctionType ? (GoFunctionType)base : null;
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

  static String updatePresentation(@Nullable Object p, @NotNull ParameterInfoUIContext context) {
    if (p == null) {
      context.setUIComponentEnabled(false);
      return null;
    }
    GoSignature signature = p instanceof GoSignatureOwner ? ((GoSignatureOwner)p).getSignature() : null;
    if (signature == null) return null;
    GoParameters parameters = signature.getParameters();
    List<String> parametersPresentations = getParameterPresentations(parameters, PsiElement::getText);
    
    StringBuilder builder = new StringBuilder();
    int start = 0;
    int end = 0;
    if (!parametersPresentations.isEmpty()) {
      // Figure out what particular presentation is actually selected. Take in
      // account possibility of the last variadic parameter.
      int selected = isLastParameterVariadic(parameters.getParameterDeclarationList())
                     ? Math.min(context.getCurrentParameterIndex(), parametersPresentations.size() - 1)
                     : context.getCurrentParameterIndex();
      
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
    }
    else {
      builder.append(CodeInsightBundle.message("parameter.info.no.parameters"));
    }
    return context.setupUIComponentPresentation(builder.toString(), start, end, false, false, false, context.getDefaultParameterColor());
  }

  /**
   * Creates a list of parameter presentations. For clarity we expand parameters declared as `a, b, c int` into `a int, b int, c int`.
   */
  @NotNull
  public static List<String> getParameterPresentations(@NotNull GoParameters parameters, 
                                                       @NotNull Function<PsiElement, String> typePresentationFunction) {
    List<GoParameterDeclaration> paramDeclarations = parameters.getParameterDeclarationList();
    List<String> paramPresentations = ContainerUtil.newArrayListWithCapacity(2 * paramDeclarations.size());
    for (GoParameterDeclaration paramDeclaration : paramDeclarations) {
      boolean isVariadic = paramDeclaration.isVariadic();
      List<GoParamDefinition> paramDefinitionList = paramDeclaration.getParamDefinitionList();
      for (GoParamDefinition paramDefinition : paramDefinitionList) {
        String separator = isVariadic ? " ..." : " ";
        paramPresentations.add(paramDefinition.getText() + separator + typePresentationFunction.fun(paramDeclaration.getType()));
      }
      if (paramDefinitionList.isEmpty()) {
        String separator = isVariadic ? "..." : "";
        paramPresentations.add(separator + typePresentationFunction.fun(paramDeclaration.getType()));
      }
    }
    return paramPresentations;
  }

  private static boolean isLastParameterVariadic(@NotNull List<GoParameterDeclaration> declarations) {
    GoParameterDeclaration lastItem = ContainerUtil.getLastItem(declarations);
    return lastItem != null && lastItem.isVariadic();
  }
}
