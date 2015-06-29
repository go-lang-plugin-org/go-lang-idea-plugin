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

package com.goide.completion;

import com.goide.psi.*;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoAutoImportInsertHandler<T extends GoNamedElement> implements InsertHandler<LookupElement> {
  public static final InsertHandler<LookupElement> TYPE_INSERT_HANDLER = new GoAutoImportInsertHandler<GoNamedElement>();
  public static final InsertHandler<LookupElement> TYPE_CONVERSION_INSERT_HANDLER = new GoAutoImportInsertHandler<GoTypeSpec>(
    new Function<GoTypeSpec, InsertHandler<LookupElement>>() {
      @Override
      public InsertHandler<LookupElement> fun(GoTypeSpec spec) {
        return GoCompletionUtil.getTypeConversionInsertHandler(spec);
      }
    }, GoTypeSpec.class);
  public static final InsertHandler<LookupElement> FUNCTION_INSERT_HANDLER = new GoAutoImportInsertHandler<GoFunctionDeclaration>(
    GoCompletionUtil.FUNCTION_INSERT_HANDLER, GoFunctionDeclaration.class);

  @Nullable private final Function<T, InsertHandler<LookupElement>> myDelegateGetter;
  @Nullable private final Class<T> myClass;

  public GoAutoImportInsertHandler() {
    this(((Function<T, InsertHandler<LookupElement>>)null), null);
  }

  public GoAutoImportInsertHandler(@Nullable final InsertHandler<LookupElement> delegate, @Nullable Class<T> clazz) {
    this(new Function<T, InsertHandler<LookupElement>>() {
      @Override
      public InsertHandler<LookupElement> fun(T o) {
        return delegate;
      }
    }, clazz);
  }


  public GoAutoImportInsertHandler(@Nullable Function<T, InsertHandler<LookupElement>> delegateGetter, @Nullable Class<T> clazz) {
    myDelegateGetter = delegateGetter;
    myClass = clazz;
  }

  @Override
  public void handleInsert(InsertionContext context, LookupElement item) {
    PsiElement element = item.getPsiElement();
    if (element instanceof GoNamedElement) {
      if (myClass != null && myDelegateGetter != null && myClass.isInstance(element)) {
        InsertHandler<LookupElement> handler = myDelegateGetter.fun(myClass.cast(element));
        if (handler != null) {
          handler.handleInsert(context, item);
        }
      }
      autoImport(context, (GoNamedElement)element);
    }
  }

  private static void autoImport(@NotNull InsertionContext context, @NotNull GoNamedElement element) {
    PsiFile file = context.getFile();
    if (!(file instanceof GoFile)) return;

    String fullPackageName = element.getContainingFile().getImportPath();
    if (StringUtil.isEmpty(fullPackageName)) return;

    GoImportSpec existingImport = ((GoFile)file).getImportedPackagesMap().get(fullPackageName);
    if (existingImport != null) return;

    PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getEditor().getDocument());
    ((GoFile)file).addImport(fullPackageName, null);
  }
}
