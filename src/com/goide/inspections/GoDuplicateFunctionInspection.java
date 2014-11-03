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

package com.goide.inspections;

import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoRecursiveVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GoDuplicateFunctionInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull final ProblemsHolder problemsHolder) {
    List<GoFunctionDeclaration> functions = file.getFunctions();
    final MultiMap<String, GoFunctionDeclaration> map = new MultiMap<String, GoFunctionDeclaration>();
    for (GoFunctionDeclaration function : functions) {
      map.putValue(function.getName(), function);
    }

    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        String name = o.getName();
        if (name == null) return;
        Collection<GoFunctionDeclaration> byKey = map.get(name);
        if (byKey.size() > 1) {
          if (o.equals(byKey.iterator().next())) return;
          PsiElement identifier = o.getNameIdentifier();
          problemsHolder.registerProblem(identifier == null ? o : identifier, "Duplicate function name");
        }
      }
    });
  }
}
