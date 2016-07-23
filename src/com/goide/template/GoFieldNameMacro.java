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

package com.goide.template;

import com.goide.psi.GoFieldDeclaration;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class GoFieldNameMacro extends Macro {
  @Override
  public String getName() {
    return "fieldName";
  }

  @Override
  public String getPresentableName() {
    return "fieldName()";
  }

  @Nullable
  @Override
  public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
    String name = ContainerUtil.getFirstItem(fieldNames(context));
    return StringUtil.isNotEmpty(name) ? new TextResult(name) : null;
  }

  @Nullable
  @Override
  public LookupElement[] calculateLookupItems(@NotNull Expression[] params, ExpressionContext context) {
    return ContainerUtil.map2Array(fieldNames(context), LookupElement.class, LookupElementBuilder::create);
  }

  @Override
  public boolean isAcceptableInContext(TemplateContextType context) {
    return context instanceof GoLiveTemplateContextType.Tag || context instanceof GoLiveTemplateContextType.TagLiteral;
  }

  private static Set<String> fieldNames(ExpressionContext context) {
    PsiElement psiElement = context != null ? context.getPsiElementAtStartOffset() : null;
    GoFieldDeclaration fieldDeclaration = PsiTreeUtil.getNonStrictParentOfType(psiElement, GoFieldDeclaration.class);
    if (fieldDeclaration == null) {
      return Collections.emptySet();
    }
    return ContainerUtil.map2LinkedSet(fieldDeclaration.getFieldDefinitionList(), PsiNamedElement::getName);
  }
}
