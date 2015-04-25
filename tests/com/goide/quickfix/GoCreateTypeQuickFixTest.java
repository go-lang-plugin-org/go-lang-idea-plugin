package com.goide.quickfix;

import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;

public class GoCreateTypeQuickFixTest extends GoQuickFixTestBase {
  public static final String CREATE_TYPE_A = "Create type 'A'";

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

  public void testSimple()      { doTest(CREATE_TYPE_A);      }
  public void testProhibited()  { doTestNoFix(CREATE_TYPE_A); }
}
