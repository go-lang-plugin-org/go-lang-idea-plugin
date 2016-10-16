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

package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.*;
import com.goide.psi.impl.GoVarProcessor;
import com.goide.quickfix.GoDeleteVarDefinitionQuickFix;
import com.goide.quickfix.GoRenameToBlankQuickFix;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoUnusedVariableInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitVarDefinition(@NotNull GoVarDefinition o) {
        if (o.isBlank()) return;
        GoCompositeElement varSpec = PsiTreeUtil.getParentOfType(o, GoVarSpec.class, GoTypeSwitchGuard.class);
        GoVarDeclaration decl = PsiTreeUtil.getParentOfType(o, GoVarDeclaration.class);
        if (shouldValidate(decl) && (varSpec != null || decl != null)) {
          PsiReference reference = o.getReference();
          PsiElement resolve = reference != null ? reference.resolve() : null;
          if (resolve != null) return;
          boolean foundReference = !ReferencesSearch.search(o, o.getUseScope()).forEach(reference1 -> {
            ProgressManager.checkCanceled();
            PsiElement element = reference1.getElement();
            if (element == null) return true;
            PsiElement parent = element.getParent();
            if (parent instanceof GoLeftHandExprList) {
              PsiElement grandParent = parent.getParent();
              if (grandParent instanceof GoAssignmentStatement &&
                  ((GoAssignmentStatement)grandParent).getAssignOp().getAssign() != null) {
                GoFunctionLit fn = PsiTreeUtil.getParentOfType(element, GoFunctionLit.class);
                if (fn == null || !PsiTreeUtil.isAncestor(GoVarProcessor.getScope(o), fn, true)) {
                  return true;
                }
              }
            }
            if (parent instanceof GoShortVarDeclaration) {
              int op = ((GoShortVarDeclaration)parent).getVarAssign().getStartOffsetInParent();
              if (element.getStartOffsetInParent() < op) {
                return true;
              }
            }
            return false;
          });

          if (!foundReference) {
            reportError(o, holder);
          }
        }
      }
    };
  }

  protected void reportError(@NotNull GoVarDefinition varDefinition, @NotNull ProblemsHolder holder) {
    holder.registerProblem(varDefinition, "Unused variable <code>#ref</code> #loc", ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                           new GoRenameToBlankQuickFix(varDefinition), new GoDeleteVarDefinitionQuickFix(varDefinition.getName()));
  }

  protected boolean shouldValidate(@Nullable GoVarDeclaration varDeclaration) {
    return varDeclaration == null || !(varDeclaration.getParent() instanceof GoFile);
  }
}
