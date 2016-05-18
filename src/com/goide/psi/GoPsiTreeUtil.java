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

package com.goide.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoPsiTreeUtil extends PsiTreeUtil {
  @Nullable
  public static <T extends PsiElement> T getStubChildOfType(@Nullable PsiElement element, @NotNull Class<T> aClass) {
    if (element == null) return null;
    StubElement<?> stub = element instanceof StubBasedPsiElement ? ((StubBasedPsiElement)element).getStub() : null;
    if (stub == null) {
      return getChildOfType(element, aClass);
    }
    for (StubElement childStub : stub.getChildrenStubs()) {
      PsiElement child = childStub.getPsi();
      if (aClass.isInstance(child)) {
        //noinspection unchecked
        return (T)child;
      }
    }
    return null;
  }

  @NotNull
  public static <T extends PsiElement> List<T> getStubChildrenOfTypeAsList(@Nullable PsiElement element, @NotNull Class<T> aClass) {
    if (element == null) return Collections.emptyList();
    StubElement<?> stub = element instanceof StubBasedPsiElement ? ((StubBasedPsiElement)element).getStub() : null;
    if (stub == null) {
      return getChildrenOfTypeAsList(element, aClass);
    }

    List<T> result = new SmartList<T>();
    for (StubElement childStub : stub.getChildrenStubs()) {
      PsiElement child = childStub.getPsi();
      if (aClass.isInstance(child)) {
        //noinspection unchecked
        result.add((T)child);
      }
    }
    return result;
  }
}
  