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
import com.goide.psi.GoSignature;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static com.goide.GoConstants.INIT;
import static com.goide.GoConstants.MAIN;

public class GoDuplicateFunctionInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull final ProblemsHolder problemsHolder) {
    final MultiMap<String, GoFunctionDeclaration> map = new MultiMap<String, GoFunctionDeclaration>();
    List<GoFile> files = GoPsiImplUtil.getAllPackageFiles(file);
    for (GoFile goFile : files) {
      for (GoFunctionDeclaration function : goFile.getFunctions()) {
        map.putValue(function.getName(), function);
      }
    }

    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitFunctionDeclaration(@NotNull final GoFunctionDeclaration o) {
        String name = o.getName();
        if (name == null) return;
        Collection<GoFunctionDeclaration> byKey = map.get(name);
        if (INIT.equals(name) & zeroArity(o)) return;
        if ((MAIN.equals(name) && MAIN.equals(o.getContainingFile().getPackageName()) && zeroArity(o))) {
          byKey = ContainerUtil.filter(byKey, new Condition<GoFunctionDeclaration>() {
            @Override
            public boolean value(GoFunctionDeclaration declaration) {
              return Comparing.equal(declaration.getContainingFile(), o.getContainingFile());
            }
          });
        }
        if (byKey.size() > 1) {
          PsiElement identifier = o.getNameIdentifier();
          problemsHolder.registerProblem(identifier == null ? o : identifier, "Duplicate function name");
        }
      }

      private boolean zeroArity(@NotNull GoFunctionDeclaration o) {
        GoSignature signature = o.getSignature();
        return signature == null || signature.getParameters().getParameterDeclarationList().isEmpty();
      }
    });
  }
}
