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

package com.goide.inspections;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoVisitor;
import com.goide.quickfix.GoRenameQuickFix;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

public class GoRedeclareImportAsFunctionInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        String functionName = o.getName();
        if (StringUtil.isNotEmpty(functionName) &&
            !"_".equals(functionName) &&
            o.getContainingFile().getImportMap().containsKey(functionName)) {
          holder.registerProblem(o.getIdentifier(), "import \"" + functionName + "\" redeclared in this block", new GoRenameQuickFix(o));
        }
      }
    };
  }
}
