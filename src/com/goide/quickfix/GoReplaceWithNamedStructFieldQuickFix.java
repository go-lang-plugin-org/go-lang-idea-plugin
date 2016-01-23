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

import com.goide.psi.GoElement;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GoReplaceWithNamedStructFieldQuickFix extends LocalQuickFixOnPsiElement {
  public static final String QUICK_FIX_NAME = "Replace with named struct field";
  private String myStructField;

  public GoReplaceWithNamedStructFieldQuickFix(@NotNull String structField, @NotNull GoElement structValue) {
    super(structValue);
    myStructField = structField;
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return QUICK_FIX_NAME;
  }

  @NotNull
  @Override
  public String getText() {
    return QUICK_FIX_NAME;
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
    if (!(startElement instanceof GoElement)) return;
    WriteCommandAction.runWriteCommandAction(project, new Runnable() {
      @Override
      public void run() {
        startElement.replace(GoElementFactory.createNamedStructField(project, myStructField, startElement.getText()));
      }
    });
  }
}