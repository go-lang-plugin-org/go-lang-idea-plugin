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

package com.goide.generate;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.runconfig.testing.GoTestFunctionType;
import com.goide.runconfig.testing.frameworks.gotest.GotestGenerateAction;
import org.jetbrains.annotations.NotNull;

public class GoGenerateTestActionTest extends GoCodeInsightFixtureTestCase {
  public void testTest() {
    doTest(GoTestFunctionType.TEST, "package test\n<caret>", "package test\n" +
                                                             "\n" +
                                                             "import \"testing\"\n" +
                                                             "func Test<caret>(t *testing.T) {\n" +
                                                             "\t\n" +
                                                             "}");
  }

  public void testBenchmark() {
    doTest(GoTestFunctionType.BENCHMARK, "package test\n<caret>", "package test\n" +
                                                                  "\n" +
                                                                  "import \"testing\"\n" +
                                                                  "func Benchmark<caret>(b *testing.B) {\n" +
                                                                  "\t\n" +
                                                                  "}");
  }

  public void testExample() {
    doTest(GoTestFunctionType.EXAMPLE, "package test\n<caret>", "package test\n" +
                                                                "func Example<caret>() {\n" +
                                                                "\t\n" +
                                                                "}");
  }

  public void testImportedTestingPackage() {
    doTest(GoTestFunctionType.TEST, "package test\n" +
                                    "import `testing`\n" +
                                    "<caret>", "package test\n" +
                                               "import `testing`\n" +
                                               "func Test<caret>(t *testing.T) {\n" +
                                               "\t\n" +
                                               "}");
  }

  public void testImportedTestingPackageWithDifferentAlias() {
    doTest(GoTestFunctionType.TEST, "package test\n" +
                                    "import my_alias `testing`\n" +
                                    "<caret>", "package test\n" +
                                               "import my_alias `testing`\n" +
                                               "func Test<caret>(t *my_alias.T) {\n" +
                                               "\t\n" +
                                               "}");
  }

  public void testImportedTestingPackageWithDot() {
    doTest(GoTestFunctionType.TEST, "package test\n" +
                                    "import . `testing`\n" +
                                    "<caret>", "package test\n" +
                                               "import . `testing`\n" +
                                               "func Test<caret>(t *T) {\n" +
                                               "\t\n" +
                                               "}");
  }

  public void testImportedTestingPackageWithForSideEffects() {
    doTest(GoTestFunctionType.TEST, "package test\n" +
                                    "import _ `testing`\n" +
                                    "<caret>", "package test\n" +
                                               "\n" +
                                               "import (\n" +
                                               "\t_ \"testing\"\n" +
                                               "\t\"testing\"\n" +
                                               ")\n" +
                                               "func Test<caret>(t *testing.T) {\n" +
                                               "\t\n" +
                                               "}");
  }

  public void testImportedPackageWithTestingAlias() {
    doTest(GoTestFunctionType.TEST, "package test\n" +
                                    "import testing `some_other_package`\n" +
                                    "<caret>", "package test\n" +
                                               "\n" +
                                               "import (\n" +
                                               "\ttesting \"some_other_package\"\n" +
                                               "\ttesting2 \"testing\"\n" +
                                               ")\n" +
                                               "func Test<caret>(t *testing2.T) {\n" +
                                               "\t\n" +
                                               "}");
  }


  private void doTest(@NotNull GoTestFunctionType type, @NotNull String beforeText, @NotNull String afterText) {
    myFixture.configureByText("test_test.go", beforeText);
    myFixture.testAction(new GotestGenerateAction(type));
    myFixture.checkResult(afterText);
  }
}
