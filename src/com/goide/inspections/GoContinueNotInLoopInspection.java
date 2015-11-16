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

import com.goide.psi.GoContinueStatement;
import com.goide.psi.GoForStatement;
import com.goide.psi.GoVisitor;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Raises an error when a 'continue' statement is found outside of a for loop.
 */
public class GoContinueNotInLoopInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitContinueStatement(@NotNull GoContinueStatement o) {
        if (PsiTreeUtil.getParentOfType(o.getContinue(), GoForStatement.class) == null) {
          holder
            .registerProblem(o.getContinue(), "Continue statement not inside a for loop.", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
      }
    };
  }
}
