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

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.stubs.index.GoFunctionIndex;
import com.goide.stubs.index.GoMethodIndex;
import com.goide.stubs.types.GoMethodDeclarationStubElementType;
import com.goide.util.GoUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import static com.goide.GoConstants.INIT;
import static com.goide.GoConstants.MAIN;

public class GoDuplicateFunctionOrMethodInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitMethodDeclaration(@NotNull final GoMethodDeclaration method) {
        if (method.isBlank()) return;

        final String methodName = method.getName();
        if (methodName == null) return;

        String typeText = GoMethodDeclarationStubElementType.calcTypeText(method);
        if (typeText == null) return;

        GoFile file = method.getContainingFile();
        GlobalSearchScope scope = GoPsiImplUtil.packageScope(file);
        GoMethodIndex.process(file.getPackageName() + "." + typeText, file.getProject(), scope, new Processor<GoMethodDeclaration>() {
          @Override
          public boolean process(GoMethodDeclaration declaration) {
            if (!method.isEquivalentTo(declaration)) {
              if (Comparing.equal(declaration.getName(), methodName) && GoUtil.allowed(declaration.getContainingFile())) {
                PsiElement identifier = method.getNameIdentifier();
                holder.registerProblem(identifier == null ? method : identifier, "Duplicate method name");
                return false;
              }
            }
            return true;
          }
        });
      }

      @Override
      public void visitFunctionDeclaration(@NotNull final GoFunctionDeclaration func) {
        if (func.isBlank()) return;

        String funcName = func.getName();
        if (funcName == null) return;
        if (INIT.equals(funcName) && zeroArity(func)) return;

        final GoFile file = func.getContainingFile();
        final boolean isMainFunction = MAIN.equals(funcName) && MAIN.equals(file.getPackageName()) && zeroArity(func);
        final GlobalSearchScope scope = GoPsiImplUtil.packageScope(file);
        GoFunctionIndex.process(funcName, file.getProject(), scope, new Processor<GoFunctionDeclaration>() {
          @Override
          public boolean process(GoFunctionDeclaration declaration) {
            if (!func.isEquivalentTo(declaration)) {
              if (!isMainFunction || Comparing.equal(declaration.getContainingFile(), file)) {
                PsiElement identifier = func.getNameIdentifier();
                holder.registerProblem(identifier == null ? func : identifier, "Duplicate function name");
                return false;
              }
            }
            return true;
          }
        });
      }
    };
  }

  private static boolean zeroArity(@NotNull GoFunctionDeclaration o) {
    GoSignature signature = o.getSignature();
    return signature == null || signature.getParameters().getParameterDeclarationList().isEmpty();
  }
}
