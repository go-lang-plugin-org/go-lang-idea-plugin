package com.goide.inspections.suppression;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.GoUnresolvedReferenceInspection;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public class GoSuppressionFixTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
  }

  public void testFunctionSuppressionFix() {
    doTest("Suppress for function");
  }

  public void testFunctionSuppressionFixWithExistingComment() {
    doTest("Suppress for function");
  }

  public void testStatementSuppressionFix() {
    doTest("Suppress for statement");
  }

  public void testStatementSuppressionFixWithExistingComment() {
    doTest("Suppress for statement");
  }

  public void testFunctionAllSuppressionFix() {
    doTest("Suppress all inspections for function");
  }

  public void testFunctionAllSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for function");
  }

  public void testStatementAllSuppressionFix() {
    doTest("Suppress all inspections for statement");
  }

  public void testStatementAllSuppressionFixWithExistingComment() {
    doTest("Suppress all inspections for statement");
  }

  private void doTest(String intentionMessage) {
    myFixture.configureByFile(getTestName(true) + ".go");
    IntentionAction action = ContainerUtil.getFirstItem(myFixture.filterAvailableIntentions(intentionMessage));
    assertNotNull(action);
    myFixture.launchAction(action);
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  @Override
  protected String getBasePath() {
    return "inspections/suppression/fix";
  }
}
