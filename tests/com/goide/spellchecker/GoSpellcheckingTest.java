package com.goide.spellchecker;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.spellchecker.inspections.SpellCheckingInspection;

public class GoSpellcheckingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(new SpellCheckingInspection());
  }

  public void testVariableName() throws Exception {
    doTest();
  }

  public void testFunctionName() throws Exception {
    doTest();
  }

  public void testSuppressed() throws Exception {
    doTest();
  }

  private void doTest() {
    myFixture.testHighlighting(false, false, true, getTestName(true) + ".go");
  }

  @Override
  protected String getBasePath() {
    return "spellchecker";
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }
}
