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

package com.goide.inspections;

import com.goide.psi.GoFile;
import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.stubs.index.GoMethodIndex;
import com.goide.stubs.types.GoMethodDeclarationStubElementType;
import com.goide.util.GoUtil;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GoDuplicateMethodInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull ProblemsHolder problemsHolder) {
    Project project = file.getProject();
    String packageName = file.getPackageName();
    GlobalSearchScope scope = GoPsiImplUtil.packageScope(file);

    for (final GoMethodDeclaration method : file.getMethods()) {
      if (method.isBlank()) continue;

      final String methodName = method.getName();
      if (methodName == null) continue;
      
      String typeText = GoMethodDeclarationStubElementType.calcTypeText(method);
      if (typeText == null) continue;

      Collection<GoMethodDeclaration> declarations = GoMethodIndex.find(packageName + "." + typeText, project, scope);
      declarations = ContainerUtil.filter(declarations, new Condition<GoMethodDeclaration>() {
        @Override
        public boolean value(@NotNull GoMethodDeclaration d) {
          return !method.isEquivalentTo(d) && Comparing.equal(d.getName(), methodName) && GoUtil.allowed(d.getContainingFile());
        }
      });

      if (declarations.isEmpty()) continue;

      PsiElement identifier = method.getNameIdentifier();
      problemsHolder.registerProblem(identifier == null ? method : identifier, "Duplicate method name");
    }
  }
}
