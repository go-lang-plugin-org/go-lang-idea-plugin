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

  public void testRunLineMater() {
    myFixture.configureByText("a.go", "package main\n" +
                                      "func m<caret>ain(){}");
    assertEquals(1, myFixture.findGuttersAtCaret().size());
    assertEquals(1, myFixture.findAllGutters().size());
  }

  public void testRunLineMarkerInNonMainFile() {
    myFixture.configureByText("a.go", "package not_main\n" +
                                      "func m<caret>ain(){}");
    assertEquals(0, myFixture.findGuttersAtCaret().size());
    assertEquals(0, myFixture.findAllGutters().size());
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }
}
