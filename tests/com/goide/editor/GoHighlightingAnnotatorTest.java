package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.testFramework.TestDataPath;

@TestDataPath("$PROJECT_ROOT/testData/colorHighlighting")
public class GoHighlightingAnnotatorTest extends GoCodeInsightFixtureTestCase {
  
  public void testSimple() {
    doTest();
  }

  public void testLabel() {
    doTest();
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  private void doTest() {
    myFixture.testHighlighting(false, true, false, getTestName(true) + ".go");
  }

  @Override
  protected String getBasePath() {
    return "colorHighlighting";
  }
}

