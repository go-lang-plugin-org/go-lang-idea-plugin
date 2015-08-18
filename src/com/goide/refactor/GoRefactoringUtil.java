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

package com.goide.refactor;

import com.goide.psi.GoBlock;
import com.goide.psi.GoStatement;
import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoRefactoringUtil {

  @NotNull
  public static List<PsiElement> getLocalOccurrences(@NotNull final PsiElement element) {
    return getOccurrences(element, PsiTreeUtil.getTopmostParentOfType(element, GoBlock.class));
  }

  @NotNull
  public static List<PsiElement> getOccurrences(@NotNull final PsiElement pattern, @Nullable PsiElement context) {
    if (context == null) return Collections.emptyList();
    final List<PsiElement> occurrences = ContainerUtil.newArrayList();
    PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
      public void visitElement(@NotNull PsiElement element) {
        if (PsiEquivalenceUtil.areElementsEquivalent(element, pattern)) {
          occurrences.add(element);
          return;
        }
        super.visitElement(element);
      }
    };
    context.acceptChildren(visitor);
    return occurrences;
  }

  @Nullable
  public static PsiElement findLocalAnchor(List<PsiElement> occurrences) {
    return findAnchor(occurrences, PsiTreeUtil.getNonStrictParentOfType(PsiTreeUtil.findCommonParent(occurrences), GoBlock.class));
  }

  @Nullable
  public static PsiElement findAnchor(List<PsiElement> occurrences, PsiElement context) {
    PsiElement statement = PsiTreeUtil.getNonStrictParentOfType(ContainerUtil.getFirstItem(occurrences), GoStatement.class);
    while (statement != null && statement.getParent() != context) {
      statement = statement.getParent();
    }
    return statement;
  }
}
