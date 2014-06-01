package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.CodeInsightTestUtil;

public class GoSelectWordTest extends GoCodeInsightFixtureTestCase {
  public void testImportString() {
    doTest();
  }

  public void testMultipleImportString() {
    doTest();
  }

  public void testBlock() {
    doTest();
  }

  private void doTest() {
    CodeInsightTestUtil.doWordSelectionTestOnDirectory(myFixture, getTestName(true), "go");
  }

  @Override
  protected String getBasePath() {
    return "selectWord";
  }
}
