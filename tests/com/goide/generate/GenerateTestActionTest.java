package com.goide.generate;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.testFramework.PlatformTestUtil;
import org.jetbrains.annotations.NotNull;

public class GenerateTestActionTest extends GoCodeInsightFixtureTestCase {
  public void testTest() throws Exception {
    doTest("GoGenerateTest", "package test\n" +
                             "func TestName(t *testing.T) {\n" +
                             " \n" +
                             "}");
  }

  public void testBenchmark() throws Exception {
    doTest("GoGenerateBenchmark", "package test\n" +
                                  "func BenchmarkName(b *testing.B) {\n" +
                                  " for i := 0; i < b.N; i++ {\n" +
                                  "  \n" +
                                  " }\n" +
                                  "}");
  }

  private void doTest(@NotNull String actionName, @NotNull String afterText) {
    myFixture.configureByText("test_test.go", "package test\n<caret>");
    PlatformTestUtil.invokeNamedAction(actionName);
    myFixture.checkResult(afterText);
  }
}
