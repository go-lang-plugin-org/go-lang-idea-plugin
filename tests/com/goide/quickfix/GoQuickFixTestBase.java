package com.goide.quickfix;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public abstract class GoQuickFixTestBase extends GoCodeInsightFixtureTestCase {
  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  protected void doTest(String quickFixName) {
    String testName = getTestName(true);
    myFixture.configureByFile(testName + ".go");
    List<IntentionAction> availableIntentions = myFixture.filterAvailableIntentions(quickFixName);
    IntentionAction action = ContainerUtil.getFirstItem(availableIntentions);
    assertNotNull(action);
    myFixture.launchAction(action);
    String after = String.format("%s-after.go", testName);
    myFixture.checkResultByFile(after);
  }
}
