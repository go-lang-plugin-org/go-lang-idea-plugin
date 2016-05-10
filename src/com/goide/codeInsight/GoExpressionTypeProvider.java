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

package com.goide.codeInsight;

import com.goide.psi.GoTopLevelDeclaration;
import com.goide.psi.GoType;
import com.goide.psi.GoTypeOwner;
import com.intellij.lang.ExpressionTypeProvider;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoExpressionTypeProvider extends ExpressionTypeProvider<GoTypeOwner> {
  @NotNull
  @Override
  public String getInformationHint(@NotNull GoTypeOwner element) {
    GoType type = element.getGoType(null);
    return StringUtil.escapeXml(StringUtil.notNullize(type != null ? type.getText() : null, "<unknown>"));
  }

  @NotNull
  @Override
  public String getErrorHint() {
    return "Selection doesn't contain a Go expression";
  }

  @NotNull
  @Override
  public List<GoTypeOwner> getExpressionsAt(@NotNull PsiElement at) {
    if (at instanceof PsiWhiteSpace && at.textMatches("\n")) {
      at = PsiTreeUtil.prevLeaf(at);
    }
    return SyntaxTraverser.psiApi().parents(at).takeWhile(Conditions.notInstanceOf(GoTopLevelDeclaration.class))
      .filter(GoTypeOwner.class).toList();
  }
}