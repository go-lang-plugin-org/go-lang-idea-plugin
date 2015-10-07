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

package com.goide.inspections.unresolved;

import com.goide.refactor.GoRefactoringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoIntroduceLocalVariableFix extends GoUnresolvedFixBase {
  public GoIntroduceLocalVariableFix(@NotNull PsiElement element, @NotNull String name) {
    super(element, name, "local variable", "go_lang_local_var_qf");
  }

  @Nullable
  @Override
  protected PsiElement findAnchor(@NotNull PsiElement reference) {
    return GoRefactoringUtil.findLocalAnchor(GoRefactoringUtil.getLocalOccurrences(reference));
  }
}
