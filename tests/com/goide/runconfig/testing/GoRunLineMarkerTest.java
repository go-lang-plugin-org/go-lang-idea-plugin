package com.goide.runconfig.testing;

import com.goide.runconfig.GoRunConfigurationTestCase;

public class GoRunLineMarkerTest extends GoRunConfigurationTestCase {
  public void testRunTestLineMarker() {
    myFixture.configureByText("a_test.go", "package m<caret>ain\n" +
                                           "func TestName(){}\n" +
                                           "func BenchmarkName(){}\n" +
                                           "func ExampleName(){}\n" +
                                           "func Hello() {}");
    assertEquals(1, myFixture.findGuttersAtCaret().size());
    assertEquals(4, myFixture.findAllGutters().size());
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }
}
