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

package com.goide.inspections;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoVarDefinition;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameProcessor;
import org.jetbrains.annotations.NotNull;

public class GoRenameQuickFix extends LocalQuickFixOnPsiElement {
  private final String myNewName;
  private final PsiElement myElement;

  protected GoRenameQuickFix(@NotNull PsiElement element, String newName) {
    super(element);
    myNewName = newName;
    myElement = element;
  }


  @Override
  public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
    if (!startElement.isWritable()) {
      return;
    }

    new RenameProcessor(project, startElement, myNewName, false, true).run();
  }

  @NotNull
  public String getFamilyName() {
    return "Go";
  }

  @NotNull
  @Override
  public String getText() {
    if (myElement instanceof GoFunctionDeclaration) {
      return "Rename function to " + myNewName;
    }
    else if (myElement instanceof GoVarDefinition) {
      return "Rename variable to " + myNewName;
    }
    else {
      return "Rename to " + myNewName;
    }
  }
}
