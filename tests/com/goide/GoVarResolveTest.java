/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

package com.goide;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.util.containers.ContainerUtil.newArrayList;

public class GoVarResolveTest extends GoCodeInsightFixtureTestCase {
  private void doTest(String text) {
    List<Integer> offsets = allOccurrences(StringUtil.replace(text, "<caret>", ""), "<usage>");
    String replace = StringUtil.replace(text, "<usage>", "");
    myFixture.configureByText("a.go", replace);
    PsiElement atCaret = myFixture.getElementAtCaret();
    List<Integer> actual = ContainerUtil.map(myFixture.findUsages(atCaret), new Function<UsageInfo, Integer>() {
      @Override
      public Integer fun(UsageInfo info) {
        return info.getNavigationOffset();
      }
    });
    assertSameElements(actual, offsets);
  }

  @NotNull
  private static List<Integer> allOccurrences(@NotNull String text, String what) {
    List<Integer> list = newArrayList();
    int index = text.indexOf(what);
    while (index >= 0) {
      list.add(index - list.size() * what.length());
      index = text.indexOf(what, index + 1);
    }
    return list;
  }

  public void testOverride() {
    doTest("package p\n" +
           "func test2() {\n" +
           "    y := 1\n" +
           "    {\n" +
           "        <caret>y, _ := 10, 1\n" +
           "        fmt.Println(<usage>y)\n" +
           "    }\n" +
           "}\n");
  }

  public void testOverride1() {
    doTest("package p\n" +
           "func test2() {\n" +
           "    <caret>y := 1\n" +
           "    <usage>y, _ := 10, 1\n" +
           "    <usage>y, x := 10, 1\n" +
           "    fmt.Println(<usage>y)\n" +
           "}\n");
  }

  public void testOverride2() {
    doTest("package p\n" +
           "func test2() {\n" +
           "    <caret>y := 1\n" +
           "    {\n" +
           "        y, _ := 10, 1\n" +
           "        fmt.Println(y)\n" +
           "    }\n" +
           "}\n");
  }

  public void testFunctionParam() {
    doTest("package p\n" +
           "func aaa(<caret>a int) {\n" +
           "    <usage>a\n" +
           "    var a int\n" +
           "    <usage>a := 1\n" +
           "}\n");
  }
  
  public void testDeclaredInForRange() {
    doTest("package main\n" +
           "const key = iota\n" +
           "func main() {\n" +
           "    key := 1\n" +
           "    for <caret>key, val := range m {\n" +
           "        y := <usage>key\n" +
           "    }\n" +
           "}");
  }
}
