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

package com.goide.highlighting;

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.quickfix.GoReplaceWithReturnStatementQuickFix;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GoAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (!(element instanceof GoCompositeElement) || !element.isValid()) {
      return;
    }

    if (element instanceof GoContinueStatement) {
      if (!(PsiTreeUtil.getParentOfType(element, GoForStatement.class, GoFunctionLit.class) instanceof GoForStatement)) {
        Annotation annotation = holder.createErrorAnnotation(element, "Continue statement not inside a for loop.");
        annotation.registerFix(new GoReplaceWithReturnStatementQuickFix(element));
      }
    }
    else if (element instanceof GoBreakStatement) {
      if (GoPsiImplUtil.getBreakStatementOwner(element) == null) {
        Annotation annotation = holder.createErrorAnnotation(element, "Break statement not inside a for loop, select or switch.");
        annotation.registerFix(new GoReplaceWithReturnStatementQuickFix(element));
      }
    }
    else if (element instanceof GoReferenceExpression) {
      GoReferenceExpression reference = (GoReferenceExpression) element;
      PsiElement resolvedReference = reference.getReference().resolve();
      if (resolvedReference instanceof PsiDirectory || resolvedReference instanceof GoImportSpec) {
        // It's a package reference. It should either be inside a package clause or part of a larger reference expression.
        if (!(element.getParent() instanceof GoReferenceExpression) &&
            PsiTreeUtil.getParentOfType(reference, GoPackageClause.class) == null) {
          holder.createErrorAnnotation(element, "Use of package " + element.getText() + " without selector.");
        }
      }
    }
  }
}
