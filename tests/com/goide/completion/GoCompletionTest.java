package com.goide.completion;

import com.goide.GoCodeInsightFixtureTestCase;

public class GoCompletionTest extends GoCodeInsightFixtureTestCase {

  public void testKeywords() throws Exception {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "const", "continue");
  }

  @Override
  protected String getBasePath() {
    return "completion";
  }
}
