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

package com.goide.template;

import com.intellij.codeInsight.template.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectoryNameMacro extends Macro {

  @NotNull
  @Override
  public String getName() {
    return "directoryName";
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return "directoryName";
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, @NotNull ExpressionContext context) {
    PsiElement psiEle = context.getPsiElementAtStartOffset();
    if (psiEle != null) {
      VirtualFile file = psiEle.getContainingFile().getVirtualFile();
      if (file != null) {
        VirtualFile parent = file.getParent();
        if (parent != null) {
          return calculateResult(parent);
        }
      }
    }
    return null;
  }

  @Nullable
  protected TextResult calculateResult(@NotNull VirtualFile virtualFile) {
    return new TextResult(virtualFile.getName());
  }
}
