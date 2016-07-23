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

package com.goide.completion;

import com.goide.codeInsight.imports.GoImportPackageQuickFix;
import com.goide.project.GoVendoringUtil;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoNamedElement;
import com.goide.psi.GoTypeSpec;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoAutoImportInsertHandler<T extends GoNamedElement> implements InsertHandler<LookupElement> {
  public static final InsertHandler<LookupElement> SIMPLE_INSERT_HANDLER = new GoAutoImportInsertHandler<>();
  public static final InsertHandler<LookupElement> TYPE_CONVERSION_INSERT_HANDLER = new GoAutoImportInsertHandler<>(
    GoCompletionUtil.Lazy.TYPE_CONVERSION_INSERT_HANDLER, GoTypeSpec.class);
  public static final InsertHandler<LookupElement> FUNCTION_INSERT_HANDLER = new GoAutoImportInsertHandler<>(
    GoCompletionUtil.Lazy.VARIABLE_OR_FUNCTION_INSERT_HANDLER, GoFunctionDeclaration.class);

  @Nullable private final InsertHandler<LookupElement> myDelegate;
  @Nullable private final Class<T> myClass;

  private GoAutoImportInsertHandler() {
    this(null, null);
  }

  private GoAutoImportInsertHandler(@Nullable InsertHandler<LookupElement> delegate, @Nullable Class<T> clazz) {
    myDelegate = delegate;
    myClass = clazz;
  }

  @Override
  public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
    PsiElement element = item.getPsiElement();
    if (element instanceof GoNamedElement) {
      if (myClass != null && myDelegate != null && myClass.isInstance(element)) {
        myDelegate.handleInsert(context, item);
      }
      autoImport(context, (GoNamedElement)element);
    }
  }

  private static void autoImport(@NotNull InsertionContext context, @NotNull GoNamedElement element) {
    PsiFile file = context.getFile();
    boolean vendoringEnabled = GoVendoringUtil.isVendoringEnabled(ModuleUtilCore.findModuleForPsiElement(file));
    String importPath = element.getContainingFile().getImportPath(vendoringEnabled);
    if (StringUtil.isEmpty(importPath)) return;

    PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getEditor().getDocument());
    PsiReference reference = file.findReferenceAt(context.getStartOffset());
    if (reference != null) {
      PsiElement referenceElement = reference.getElement();
      GoImportPackageQuickFix fix = new GoImportPackageQuickFix(referenceElement, importPath);
      fix.invoke(context.getProject(), file, context.getEditor(), referenceElement, referenceElement);
    }
  }
}
