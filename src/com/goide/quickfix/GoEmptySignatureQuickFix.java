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

package com.goide.quickfix;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoSignature;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoEmptySignatureQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
  public static final String QUICK_FIX_NAME = "Fix signature";

  public GoEmptySignatureQuickFix(@NotNull GoFunctionDeclaration functionDeclaration) {
    super(functionDeclaration);
  }

  @NotNull
  @Override
  public String getText() {
    return QUICK_FIX_NAME;
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return QUICK_FIX_NAME;
  }

  @Override
  public void invoke(@NotNull Project project,
                     @NotNull PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement,
                     @NotNull PsiElement endElement) {
    GoFunctionDeclaration function = ObjectUtils.tryCast(startElement, GoFunctionDeclaration.class);
    GoSignature signature = function != null ? function.getSignature() : null;
    if (signature == null) return;
    signature.replace(GoElementFactory.createFunctionSignatureFromText(project, ""));
  }
}