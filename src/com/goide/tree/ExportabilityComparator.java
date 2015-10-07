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

package com.goide.tree;

import com.goide.psi.GoNamedElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ExportabilityComparator implements Comparator {
  public static final Comparator INSTANCE = new ExportabilityComparator();

  @Override
  public int compare(@NotNull Object descriptor1, @NotNull Object descriptor2) {
    int accessLevel1 = getAccessLevel(descriptor1);
    int accessLevel2 = getAccessLevel(descriptor2);
    return accessLevel2 - accessLevel1;
  }

  private static int getAccessLevel(@NotNull Object element) {
    if (element instanceof GoStructureViewFactory.Element) {
      PsiElement value = ((GoStructureViewFactory.Element)element).getValue();
      if (value instanceof GoNamedElement && ((GoNamedElement)value).isPublic()) return 1;
    }
    return -1;
  }
}
