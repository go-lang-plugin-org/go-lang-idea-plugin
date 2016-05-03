package com.goide.runconfig.testing;

import com.goide.runconfig.GoRunConfigurationTestCase;

public class GoRunLineMarkerTest extends GoRunConfigurationTestCase {
  public void testRunTestLineMarker() {
    myFixture.configureByText("a_test.go", "package m<caret>ain\n" +
                                           "import .`gopkg.in/check.v1`\n" +
                                           "func TestName(){}\n" +
                                           "func BenchmarkName(){}\n" +
                                           "func ExampleName(){}\n" +
                                           "type MySuite struct{}\n" +
                                           "var _ = Suite(&MySuite{})\n" +
                                           "func (s *MySuite) TestHelloWorld(c *C) {}\n" +
                                           "func Hello() {}");
    assertEquals(1, myFixture.findGuttersAtCaret().size());
    assertEquals(5, myFixture.findAllGutters().size());
  }

  public void testRunLineMarker() {
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
