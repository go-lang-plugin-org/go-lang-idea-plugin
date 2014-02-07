package com.goide.quickfix;

import com.goide.inspections.GoUnresolvedReferenceInspection;

public class GoCreateLocalVariableQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    //noinspection unchecked
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
  }

  @Override
  protected String getTestDataPath() {
    return "testData/quickfixes/local-variable/";
  }

  public void testSimple() { doTest("Create local variable 'a'"); }
  public void testIf()     { doTest("Create local variable 'a'"); }
}
