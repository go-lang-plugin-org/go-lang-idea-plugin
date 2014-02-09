package com.goide.quickfix;

import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;

public class GoCreateGlobalConstQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    //noinspection unchecked
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
  }

  @Override
  protected String getTestDataPath() {
    return "testData/quickfixes/global-constant/";
  }

  public void testSimple() { doTest("Create global constant 'a'"); }
}