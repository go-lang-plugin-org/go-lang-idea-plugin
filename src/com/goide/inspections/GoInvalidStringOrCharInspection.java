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

import com.goide.psi.GoLiteral;
import com.goide.psi.GoStringLiteral;
import com.goide.psi.GoVisitor;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;

public class GoInvalidStringOrCharInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {

      @Override
      public void visitStringLiteral(@NotNull GoStringLiteral o) {
        PsiElement s = o.getString();
        if (s instanceof LeafPsiElement) {
          int length = ((LeafPsiElement)s).getCachedLength();
          if (length == 1 || ((LeafPsiElement)s).charAt(length - 1) != '"') {
            holder.registerProblem(s, "New line in string");
          }
        }
      }

      @Override
      public void visitLiteral(@NotNull GoLiteral o) {
        PsiElement c = o.getChar();
        if (c instanceof LeafPsiElement) {
          int length = ((LeafPsiElement)c).getCachedLength();
          if (length == 3 && ((LeafPsiElement)c).charAt(1) == '\'') {
            holder.registerProblem(c, "Empty character literal or unescaped ' in character literal");
          }
          if (length < 3) {
            holder.registerProblem(c, "Missing '");
          }
        }
      }
    };
  }
}
