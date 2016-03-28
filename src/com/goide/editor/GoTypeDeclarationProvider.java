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

package com.goide.editor;

import com.goide.psi.GoNamedElement;
import com.goide.psi.GoType;
import com.goide.psi.GoTypeReferenceExpression;
import com.intellij.codeInsight.navigation.actions.TypeDeclarationProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoTypeDeclarationProvider implements TypeDeclarationProvider {
  @Nullable
  @Override
  public PsiElement[] getSymbolTypeDeclarations(@NotNull PsiElement element) {
    if (!(element instanceof GoNamedElement)) return PsiElement.EMPTY_ARRAY;
    GoType type = ((GoNamedElement)element).getGoType(null);
    GoTypeReferenceExpression ref = type != null ? type.getTypeReferenceExpression() : null;
    PsiElement resolve = ref != null ? ref.resolve() : type; // todo: think about better fallback instead of `type`
    return resolve != null ? new PsiElement[]{resolve} : PsiElement.EMPTY_ARRAY;
  }
}
