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

package com.goide.inspections;

import com.goide.psi.*;
import com.goide.stubs.index.GoMethodIndex;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GoDuplicateMethodInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull final ProblemsHolder problemsHolder) {
    final PsiDirectory parent = file.getParent();
    if (parent == null) return;
    final Project project = file.getProject();
    final String packageName = file.getPackageName();
    final GlobalSearchScope scope = GlobalSearchScopesCore.directoryScope(parent, false);

    for (GoMethodDeclaration m : file.getMethods()) {
      if (m.isBlank()) {
        continue;
      }

      GoType type = m.getReceiver().getType();
      if (type == null) continue;

      final String methodName = m.getName();
      if (methodName == null) continue;

      String key = packageName + "." + type.getText();
      Collection<GoMethodDeclaration> declarations = GoMethodIndex.find(key, project, scope);

      declarations = ContainerUtil.filter(declarations, new Condition<GoMethodDeclaration>() {
        @Override
        public boolean value(GoMethodDeclaration d) {
          return Comparing.equal(d.getName(), methodName);
        }
      });

      if (declarations.size() <= 1) continue;

      PsiElement identifier = m.getNameIdentifier();
      problemsHolder.registerProblem(identifier == null ? m : identifier, "Duplicate method name");
    }
  }
}
