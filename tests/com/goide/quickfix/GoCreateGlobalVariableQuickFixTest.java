package com.goide.quickfix;

import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;

public class GoCreateGlobalVariableQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    //noinspection unchecked
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
  }

  @Override
  protected String getTestDataPath() {
    return "testData/quickfixes/global-variable/";
  }

  public void testSimple() { doTest("Create global variable 'a'"); }
}
