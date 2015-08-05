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

package com.goide.inspections.unresolved;

import com.goide.psi.GoTypeReferenceExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoIntroduceTypeFix extends GoUnresolvedFixBase {
  public GoIntroduceTypeFix(@NotNull PsiElement element, @NotNull String name) {
    super(element, name, "type", "go_lang_type_qf");
  }

  @Nullable
  @Override
  protected PsiElement getReferenceExpression(@NotNull PsiElement element) {
    return PsiTreeUtil.getNonStrictParentOfType(element, GoTypeReferenceExpression.class);
  }
}
