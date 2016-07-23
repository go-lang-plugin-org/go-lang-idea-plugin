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

package com.goide.codeInsight.unwrap;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.unwrap.UnwrapHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GoUnwrapTestCase extends GoCodeInsightFixtureTestCase {
  protected void assertUnwrapped(@NotNull String codeBefore, @NotNull String codeAfter) {
    assertUnwrapped(codeBefore, codeAfter, 0);
  }

  protected void assertUnwrapped(@NotNull String codeBefore, @NotNull String codeAfter, int option) {
    myFixture.configureByText("a.go", normalizeCode(codeBefore));
    UnwrapHandler h = new UnwrapHandler() {
      @Override
      protected void selectOption(List<AnAction> options, Editor editor, PsiFile file) {
        if (options.isEmpty()) return;
        options.get(option).actionPerformed(null);
      }
    };
    h.invoke(getProject(), myFixture.getEditor(), myFixture.getFile());
    myFixture.checkResult(normalizeCode(codeAfter));
  }

  protected void assertOptions(@NotNull String code, String... expectedOptions) {
    myFixture.configureByText("a.go", normalizeCode(code));
    List<String> actualOptions = ContainerUtil.newArrayList();
    UnwrapHandler h = new UnwrapHandler() {
      @Override
      protected void selectOption(List<AnAction> options, Editor editor, PsiFile file) {
        actualOptions.addAll(options.stream().map(each -> each.getTemplatePresentation().getText()).collect(Collectors.toList()));
      }
    };
    h.invoke(getProject(), myFixture.getEditor(), myFixture.getFile());
    assertOrderedEquals(actualOptions, expectedOptions);
  }
}