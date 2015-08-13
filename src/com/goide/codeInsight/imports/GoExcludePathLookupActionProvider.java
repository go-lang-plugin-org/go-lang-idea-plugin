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

package com.goide.codeInsight.imports;

import com.goide.psi.GoFile;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupActionProvider;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementAction;
import com.intellij.psi.PsiElement;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;

public class GoExcludePathLookupActionProvider implements LookupActionProvider {
  @Override
  public void fillActions(LookupElement element, Lookup lookup, Consumer<LookupElementAction> consumer) {
    PsiElement psiElement = element.getPsiElement();
    if (psiElement == null) return;

    String importPath = ((GoFile)psiElement.getContainingFile()).getImportPath();
    if (importPath != null) {
      consumer.consume(new ExcludePathAction(importPath));
    }
  }

  private static class ExcludePathAction extends LookupElementAction {
    private String myImportPath;

    protected ExcludePathAction(@NotNull String importPath) {
      super(null, "Exclude " + importPath);
      myImportPath = importPath;
    }

    @Override
    public Result performLookupAction() {
      GoCodeInsightSettings.getInstance().excludePath(myImportPath);
      return Result.HIDE_LOOKUP;
    }
  }
}
