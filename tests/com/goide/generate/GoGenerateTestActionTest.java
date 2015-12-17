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

package com.goide.generate;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.testFramework.PlatformTestUtil;
import org.jetbrains.annotations.NotNull;

public class GoGenerateTestActionTest extends GoCodeInsightFixtureTestCase {
  public void testTest() {
    doTest("GoGenerateTest", "package test\n\n" +
                             "func TestName(t *testing.T) {\n" +
                             "\t\n" +
                             "}");
  }

  public void testBenchmark() {
    doTest("GoGenerateBenchmark", "package test\n\n" +
                                  "func BenchmarkName(b *testing.B) {\n" +
                                  "\tfor i := 0; i < b.N; i++ {\n" +
                                  "\t\t\n" +
                                  "\t}\n" +
                                  "}");
  }

  private void doTest(@NotNull String actionName, @NotNull String afterText) {
    myFixture.configureByText("test_test.go", "package test\n<caret>");
    PlatformTestUtil.invokeNamedAction(actionName);
    myFixture.checkResult(afterText);
  }
}
