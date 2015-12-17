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

package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.editor.surround.GoSurroundDescriptor;
import com.intellij.codeInsight.generation.surroundWith.SurroundWithHandler;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.NotNull;

public class GoSurroundDescriptorTest extends GoCodeInsightFixtureTestCase {
  private static final Surrounder PARENTHESIS_SURROUNDER = new GoSurroundDescriptor.GoParenthesisSurrounder();

  public void testParenthesis() {
    doTest(PARENTHESIS_SURROUNDER,
           "package main; func main() {a := <selection>1 + 2</selection>}",
           "package main; func main() {a := (1 + 2)\n}"
    );
  }

  public void testNoParenthesis() {
    doTest(PARENTHESIS_SURROUNDER,
           "package main; func main() {<selection>a := 1 + 2</selection>}",
           "package main; func main() {<selection>a := 1 + 2</selection>}"
    );
  }

  private void doTest(@NotNull Surrounder surrounder, @NotNull String before, @NotNull String after) {
    myFixture.configureByText("a.go", before);
    perform(surrounder);
    myFixture.checkResult(after);
  }

  private void perform(@NotNull final Surrounder surrounder) {
    WriteCommandAction.runWriteCommandAction(null, new Runnable() {
      @Override
      public void run() {
        SurroundWithHandler.invoke(getProject(), myFixture.getEditor(), myFixture.getFile(), surrounder);
        PsiDocumentManager.getInstance(getProject()).commitDocument(myFixture.getEditor().getDocument());
        PsiDocumentManager.getInstance(getProject())
          .doPostponedOperationsAndUnblockDocument(myFixture.getDocument(myFixture.getFile()));
      }
    });
  }
}
