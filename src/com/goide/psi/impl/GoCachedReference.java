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

package com.goide.psi.impl;

import com.goide.util.GoUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoCachedReference<T extends PsiElement> extends PsiReferenceBase<T> {
  protected GoCachedReference(@NotNull T element) {
    super(element, TextRange.from(0, element.getTextLength()));
  }

  private static final ResolveCache.AbstractResolver<PsiReferenceBase, PsiElement> MY_RESOLVER =
    new ResolveCache.AbstractResolver<PsiReferenceBase, PsiElement>() {
      @Override
      public PsiElement resolve(@NotNull PsiReferenceBase base, boolean b) {
        return ((GoCachedReference)base).resolveInner();
      }
    };

  @Nullable
  protected abstract PsiElement resolveInner();

  @Nullable
  @Override
  public final PsiElement resolve() {
    return myElement.isValid()
           ? ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, MY_RESOLVER, false, false)
           : null;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return GoUtil.couldBeReferenceTo(element, myElement) && super.isReferenceTo(element);
  }
}
