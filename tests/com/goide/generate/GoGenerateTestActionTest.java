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
