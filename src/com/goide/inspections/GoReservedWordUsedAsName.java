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
import com.goide.sdk.GoSdkUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

public class GoReservedWordUsedAsName extends GoInspectionBase {
  private GoFile builtin;

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        check(o, holder);
      }

      @Override
      public void visitVarDefinition(@NotNull GoVarDefinition o) {
        check(o, holder);
      }
    };
  }

  protected void check(@NotNull GoFunctionDeclaration goFunction, @NotNull ProblemsHolder holder) {
    if (builtin == null) {
      builtin = GoSdkUtil.findBuiltinFile(goFunction);
      if (builtin == null) {
        return;
      }
    }

    String functionName = goFunction.getName();
    if (functionName == null) {
      return;
    }

    for (GoFunctionDeclaration builtinFunctionDeclaration : builtin.getFunctions()) {
      if (functionName.equals(builtinFunctionDeclaration.getName())) {
        String prefix = "my";
        if (goFunction.isPublic()) {
          prefix = "My";
        }
        String newFunctionName = prefix + functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
        holder.registerProblem(goFunction.getIdentifier(), errorTextFunction(functionName), ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                               new GoRenameQuickFix(goFunction, newFunctionName));
        break;
      }
    }
  }

  protected void check(@NotNull GoVarDefinition goVarDefinition, @NotNull ProblemsHolder holder) {
    if (builtin == null) {
      builtin = GoSdkUtil.findBuiltinFile(goVarDefinition);
      if (builtin == null) {
        return;
      }
    }

    String varName = goVarDefinition.getName();
    if (varName == null) {
      return;
    }

    for (GoTypeSpec builtinTypeDeclaration : builtin.getTypes()) {
      if (varName.equals(builtinTypeDeclaration.getName())) {
        String prefix = "my";
        if (goVarDefinition.isPublic()) {
          prefix = "My";
        }
        String newVarName = prefix + varName.substring(0, 1).toUpperCase() + varName.substring(1);
        holder.registerProblem(goVarDefinition.getIdentifier(), errorVarDeclaration(varName), ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                               new GoRenameQuickFix(goVarDefinition, newVarName));
        break;
      }
    }
  }

  private static String errorTextFunction(@NotNull String name) {
    return "Function '" + name + "' collides with builtin function '" + name + "'";
  }

  private static String errorVarDeclaration(@NotNull String name) {
    return "Variable '" + name + "' collides with builtin type '" + name + "'";
  }
}
