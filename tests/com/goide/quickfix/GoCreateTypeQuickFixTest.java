package com.goide.quickfix;

import com.goide.inspections.GoUnresolvedReferenceInspection;
import com.intellij.codeInsight.intention.IntentionAction;

import java.util.List;

public class GoCreateTypeQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    //noinspection unchecked
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
  }

  @Override
  protected String getTestDataPath() {
    return "testData/quickfixes/create-type/";
  }

  public void testSimple() { doTest("Create type 'A'"); }

  public void testProhibited() {
    String testName = getTestName(true);
    myFixture.configureByFile(testName + ".go");
    List<IntentionAction> availableIntentions = myFixture.filterAvailableIntentions("Create type 'A'");
    assertEmpty(availableIntentions);
  }
}
