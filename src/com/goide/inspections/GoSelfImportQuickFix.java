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
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GoSelfImportQuickFix extends LocalQuickFixBase {

  protected GoSelfImportQuickFix(@NotNull String name) {
    super(name);
  }

  @Override
  public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor descriptor) {
    final PsiElement element = descriptor.getPsiElement();
    final PsiFile file = element != null ? element.getContainingFile() : null;
    if (!(element instanceof GoImportSpec && file instanceof GoFile)) return;

    final Map<GoReferenceExpressionBase, String> usages = ContainerUtil.newHashMap();
    file.acceptChildren(new PsiRecursiveElementVisitor() {
      public void visitElement(@NotNull PsiElement element) {
        if (element instanceof GoReferenceExpression || element instanceof GoTypeReferenceExpression) {
          PsiReference reference = element.getReference();
          PsiElement definition = reference != null ? reference.resolve() : null;
          PsiFile definitionFile = definition instanceof GoNamedElement ? definition.getContainingFile() : null;
          if (definitionFile != null && definitionFile.getParent() == file.getParent()) {
            usages.put((GoReferenceExpressionBase)element, ((GoNamedElement)definition).getName());
            return;
          }
        }
        super.visitElement(element);
      }
    });
    WriteCommandAction.runWriteCommandAction(project, new Runnable() {
      @Override
      public void run() {
        for (Map.Entry<GoReferenceExpressionBase, String> usage : usages.entrySet()) {
          GoReferenceExpressionBase expression = usage.getKey();
          expression.replace(expression instanceof GoReferenceExpression
                                      ? GoElementFactory.createReferenceExpression(project, usage.getValue())
                                      : GoElementFactory.createTypeReferenceExpression(project, usage.getValue()));
        }
        GoImportDeclaration importDeclaration = PsiTreeUtil.getParentOfType(element, GoImportDeclaration.class);
        assert importDeclaration != null;
        PsiElement elementToDelete = importDeclaration.getImportSpecList().size() == 1 ? importDeclaration : element;
        elementToDelete.delete();
      }
    });
  }
}
