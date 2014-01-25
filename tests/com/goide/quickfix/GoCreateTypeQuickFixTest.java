package com.goide.quickfix;

import com.goide.inspections.GoUnresolvedReferenceInspection;

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

  public void testSimple() throws Throwable { doTest("Create type 'A'"); }}
