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

package com.goide.codeInsight.unwrap;

import com.goide.psi.GoElseStatement;
import com.goide.psi.GoIfStatement;
import com.intellij.psi.PsiElement;

import java.util.Set;

public abstract class GoElseUnwrapperBase extends GoUnwrapper {
  public GoElseUnwrapperBase(String description) {
    super(description);
  }

  @Override
  public void collectElementsToIgnore(PsiElement element, Set<PsiElement> result) {
    PsiElement parent = element.getParent();
    if (parent instanceof GoIfStatement) {
      result.add(parent);
    }
  }

  @Override
  public boolean isApplicableTo(PsiElement e) {
    return e instanceof GoElseStatement;
  }
}