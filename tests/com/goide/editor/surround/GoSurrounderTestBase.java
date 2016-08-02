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

package com.goide.editor.surround;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.SdkAware;
import com.intellij.codeInsight.generation.surroundWith.SurroundWithHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SdkAware
public abstract class GoSurrounderTestBase extends GoCodeInsightFixtureTestCase {

  protected void doTest(@NotNull String codeBefore,
                        @NotNull String expectedCodeAfter,
                        @NotNull String surrounderDescription,
                        boolean apply) {
    PsiFile file = myFixture.configureByText("a.go", normalizeCode(codeBefore));
    List<AnAction> applicable = SurroundWithHandler.buildSurroundActions(myFixture.getProject(), myFixture.getEditor(), file, null);
    if (applicable == null) {
      assertFalse(apply);
      return;
    }
    String suffix = ". " + surrounderDescription;
    for (AnAction action : applicable) {
      String actionPresentation = action.getTemplatePresentation().getText();
      if (actionPresentation != null && StringUtil.endsWith(actionPresentation, suffix)) {
        assertTrue(apply);
        myFixture.testAction(action);
        return;
      }
    }
    if (apply) {
      myFixture.checkResult(normalizeCode(expectedCodeAfter), true);
    }
  }
}
