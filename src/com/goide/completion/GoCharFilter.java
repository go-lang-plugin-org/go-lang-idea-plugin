/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.completion;

import com.goide.psi.GoImportString;
import com.intellij.codeInsight.lookup.CharFilter;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoCharFilter extends CharFilter {
  @Nullable
  @Override
  public Result acceptChar(char c, int prefixLength, @NotNull Lookup lookup) {
    if (lookup.isCompletion() && '/' == c && PsiTreeUtil.getParentOfType(lookup.getPsiElement(), GoImportString.class) != null) {
      return Result.ADD_TO_PREFIX;
    }
    return null;
  }
}
