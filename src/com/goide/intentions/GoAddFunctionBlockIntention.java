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

package com.goide.intentions;

import com.goide.editor.smart.GoSmartEnterProcessor;
import com.goide.psi.GoBlock;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GoAddFunctionBlockIntention extends BaseElementAtCaretIntentionAction {
  public static final String NAME = "Add function body";

  public GoAddFunctionBlockIntention() {
    setText(NAME);
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return NAME;
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
    PsiElement parent = element.getParent();
    return parent instanceof GoFunctionOrMethodDeclaration && ((GoFunctionOrMethodDeclaration)parent).getBlock() == null;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
    PsiElement parent = element.getParent();
    if (parent instanceof GoFunctionOrMethodDeclaration) {
      GoBlock block = ((GoFunctionOrMethodDeclaration)parent).getBlock();
      if (block == null) {
        GoBlock newBlock = ObjectUtils.tryCast(parent.add(GoElementFactory.createBlock(project)), GoBlock.class);
        if (newBlock != null) {
          PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
          new GoSmartEnterProcessor.PlainEnterProcessor().doEnter(newBlock, newBlock.getContainingFile(), editor, false);
        }
      }
    }
  }
}
