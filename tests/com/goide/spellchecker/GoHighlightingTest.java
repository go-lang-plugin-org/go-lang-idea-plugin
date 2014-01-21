package com.goide.spellchecker;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.GoUnresolvedReferenceInspection;

public class GoHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(new GoUnresolvedReferenceInspection());
  }

  private void doTest() throws Exception {
    myFixture.testHighlighting(true, false, false, getTestName(true) + ".go");
  }

  @Override
  protected String getBasePath() {
    return "highlighting";
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  public void testSimple() throws Exception { doTest(); }
  public void testStruct() throws Exception { doTest(); }
}
