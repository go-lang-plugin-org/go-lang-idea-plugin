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
import com.intellij.codeInsight.highlighting.HighlightUsagesHandler;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GoExitPointsHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
  }

  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  public void testBasicExitPoints() {
    String text = "package main;\n" +
                  "func bar(x int) int {\n" +
                  "  if (x < 10) {\n" +
                  "    retur<caret>n -1" +
                  "  }" +
                  "  return x\n" +
                  "}";
    doTest(text, "return -1", "return x");
  }

  public void testCaretOnFuncWithReturnAndPanic() {
    String text = "package main;\n" +
                  "fun<caret>c bar(x int) int {\n" +
                  "  if (9 < 10) {\n" +
                  "    return -1" +
                  "  }" +
                  "  panic(x)\n" +
                  "}";
    doTest(text, "func", "return -1", "panic(x)");
  }

  public void testCaretOnFuncWithNoExitPoints() {
    String text = "package main\n" +
                  "import \"fmt\"\n" +
                  "f<caret>unc main() {\n" +
                  "  fmt.Println(\"Hello, world!\"\n" +
                  "}";
    doTest(text, "func");
  }

  public void testCaretOnFuncOnType() {
    String text = "package main\n" +
                  "\n" +
                  "type Demo struct {}\n" +
                  "\n" +
                  "f<caret>unc (a Demo) demo() int {\n" +
                  "    return -1\n" +
                  "}";
    doTest(text, "func", "return -1");
  }

  private void doTest(@NotNull String text, String... usages) {
    myFixture.configureByText("foo.go", text);
    @SuppressWarnings("unchecked")
    HighlightUsagesHandlerBase<PsiElement> handler = HighlightUsagesHandler.createCustomHandler(myFixture.getEditor(), myFixture.getFile());
    assertNotNull(handler);
    List<PsiElement> targets = handler.getTargets();
    assertEquals(1, targets.size());

    handler.computeUsages(targets);
    List<TextRange> readUsages = handler.getReadUsages();
    assertEquals(usages.length, readUsages.size());

    List<String> textUsages = new ArrayList<String>();
    for (TextRange usage : readUsages) {
      String usageText = myFixture.getFile().getText().substring(usage.getStartOffset(), usage.getEndOffset());
      textUsages.add(usageText);
    }
    assertSameElements(textUsages, Arrays.asList(usages));
  }
}
