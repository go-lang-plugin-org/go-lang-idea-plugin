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

package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.SdkAware;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandler;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SdkAware
public class GoExitPointsHighlightingTest extends GoCodeInsightFixtureTestCase {
  public void testBasicExitPoints() {
    String text = "package main;\n" +
                  "func bar(x int) int {\n" +
                  "  if (x < 10) {\n" +
                  "    retur<caret>n -1" +
                  "  }" +
                  "  return x\n" +
                  "}";
    doTest(text, "func", "return -1", "return x");
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

  public void testBreakSwitch() {
    String text = "package main\n" +
                  "import \"fmt\"\n" +
                  "\n" +
                  "type Demo struct {}\n" +
                  "\n" +
                  "\n" +
                  "func (a Demo) demo() int {\n" +
                  "    i := 2\n" +
                  "    n := 10\n" +
                  "    var j int\n" +
                  "    var m int = 10\n" +
                  "    var a [][]int\n" +
                  "    var item = 10\n" +
                  "    for i = 0; i < n; i++ {\n" +
                  "        for j = 0; j < m; j++ {\n" +
                  "            switch a[i][j] {\n" +
                  "            case nil:\n" +
                  "                fmt.Println()\n" +
                  "                break\n" +
                  "            case item:\n" +
                  "                fmt.Println()\n" +
                  "                br<caret>eak\n" +
                  "            }\n" +
                  "        }\n" +
                  "    }\n" +
                  "    return -1\n" +
                  "}";
    doTest(text, "break", "break", "switch");
  }

  public void testBreakFor() {
    String text = "package main\n" +
                  "import \"fmt\"\n" +
                  "\n" +
                  "type Demo struct {}\n" +
                  "\n" +
                  "\n" +
                  "func (a Demo) demo() int {\n" +
                  "    i := 2\n" +
                  "    n := 10\n" +
                  "    var j int\n" +
                  "    var m int = 10\n" +
                  "    for i = 0; i < n; i++ {\n" +
                  "        fo<caret>r j = 0; j < m; j++ {\n" +
                  "            if j < 10 {\n" +
                  "                break\n" +
                  "            }\n" +
                  "        }\n" +
                  "    }\n" +
                  "    return -1\n" +
                  "}";
    doTest(text, "for", "break");
  }

  public void testBreakSelect() {
    String text = "package main\n" +
                  "\n" +
                  "import \"fmt\"\n" +
                  "\n" +
                  "func main() {\n" +
                  "    fmt.Println(\"FOO\")\n" +
                  "    c1 := make(chan string)\n" +
                  "    c2 := make(chan string)\n" +
                  "    for i := 0; i < 2; i++ {\n" +
                  "        sele<caret>ct {\n" +
                  "        case msg1 := <-c1:\n" +
                  "            fmt.Println(\"received\", msg1)\n" +
                  "            break\n" +
                  "        case msg2 := <-c2:\n" +
                  "            fmt.Println(\"received\", msg2)\n" +
                  "        }\n" +
                  "    }\n" +
                  "}\n";
    doTest(text, "select", "break");
  }

  public void testBreakWithLabel() {
    String text = "package main\n" +
                  "\n" +
                  "func main() {\n" +
                  "\n" +
                  "    L:\n" +
                  "    for i := 0; i < 2; i++ {\n" +
                  "        switch i {\n" +
                  "        case 2:\n" +
                  "            break\n" +
                  "        case 1:\n" +
                  "            b<caret>reak L\n" +
                  "        default:\n" +
                  "            break L\n" +
                  "        }\n" +
                  "    }\n" +
                  "}\n";
    doTest(text, "L", "break L", "break L");
  }

  private void doTest(@NotNull String text, String... usages) {
    myFixture.configureByText("foo.go", text);
    @SuppressWarnings("unchecked")
    HighlightUsagesHandlerBase<PsiElement> handler = HighlightUsagesHandler.createCustomHandler(myFixture.getEditor(), myFixture.getFile());
    assertNotNull(handler);
    List<PsiElement> targets = handler.getTargets();
    assertEquals(1, targets.size());
    handler.computeUsages(targets);
    List<String> textUsages = ContainerUtil.map(handler.getReadUsages(), range -> range.substring(myFixture.getFile().getText()));
    assertSameElements(textUsages, usages);
  }
}
