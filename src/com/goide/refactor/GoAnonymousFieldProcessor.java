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

package com.goide.refactor;

import com.goide.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.util.ObjectUtils;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GoAnonymousFieldProcessor extends RenamePsiElementProcessor {
  @Override
  public boolean canProcessElement(@NotNull PsiElement element) {
    return
      element instanceof GoTypeSpec ||
      element instanceof GoAnonymousFieldDefinition;
  }

  @Override
  public void prepareRenaming(PsiElement element, String newName, @NotNull Map<PsiElement, String> allRenames, @NotNull SearchScope scope) {
    if (element instanceof GoTypeSpec) {
      Query<PsiReference> search = ReferencesSearch.search(element, scope);
      for (PsiReference ref : search) {
        PsiElement refElement = ref.getElement();
        PsiElement type = refElement == null ? null : refElement.getParent();
        if (!(type instanceof GoType)) continue;
        PsiElement typeParent = type.getParent();
        GoPointerType pointer = ObjectUtils.tryCast(typeParent, GoPointerType.class);
        PsiElement anon = pointer != null ? pointer.getParent() : typeParent;
        if (anon instanceof GoAnonymousFieldDefinition) {
          allRenames.put(anon, newName);
        }
      }
    }
    else if (element instanceof GoAnonymousFieldDefinition) {
      GoTypeReferenceExpression reference = ((GoAnonymousFieldDefinition)element).getTypeReferenceExpression();
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (resolve instanceof GoTypeSpec) {
        allRenames.put(resolve, newName);
      }
    }
  }
}
