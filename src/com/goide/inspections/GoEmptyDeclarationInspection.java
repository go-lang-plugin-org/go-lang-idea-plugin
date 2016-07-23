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

import com.goide.GoDocumentationProvider;
import com.goide.psi.*;
import com.goide.quickfix.GoDeleteQuickFix;
import com.intellij.codeInspection.CleanupLocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;


public class GoEmptyDeclarationInspection extends GoInspectionBase implements CleanupLocalInspectionTool {

  public final static String QUICK_FIX_NAME = "Delete empty declaration";

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitConstDeclaration(@NotNull GoConstDeclaration o) {
        visitDeclaration(o);
      }

      @Override
      public void visitVarDeclaration(@NotNull GoVarDeclaration o) {
        if (o.getParent() instanceof GoFile) {
          visitDeclaration(o);
        }
      }

      @Override
      public void visitTypeDeclaration(@NotNull GoTypeDeclaration o) {
        visitDeclaration(o);
      }

      @Override
      public void visitImportDeclaration(@NotNull GoImportDeclaration o) {
        visitDeclaration(o);
      }

      private void visitDeclaration (PsiElement o) {
        if (o.getChildren().length == 0 &&
            GoDocumentationProvider.getCommentsForElement(o instanceof GoImportDeclaration && o.getPrevSibling() == null ?
                                                          o.getParent() : o).isEmpty() &&
            PsiTreeUtil.findChildrenOfType(o, PsiComment.class).isEmpty()) {
          holder.registerProblem(o, "Empty declaration <code>#ref</code> #loc", ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                                 new GoDeleteQuickFix(QUICK_FIX_NAME, o.getClass()));
        }
      }
    };
  }
}
