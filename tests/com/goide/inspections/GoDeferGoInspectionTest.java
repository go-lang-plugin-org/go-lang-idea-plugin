package com.goide.inspections;

import com.goide.quickfix.GoQuickFixTestBase;
import org.jetbrains.annotations.NotNull;

public class GoDeferGoInspectionTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoDeferGoInspection.class);
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  public void testParens() {
    doTest(GoDeferGoInspection.QUICK_FIX_NAME, true);
  }

  public void testLiteral() {
    doTestNoFix(GoDeferGoInspection.QUICK_FIX_NAME, true);
  }

  public void testFuncLiteral() {
    doTest(GoDeferGoInspection.QUICK_FIX_NAME, true);
  }

  public void testValid() {
    myFixture.testHighlighting(getTestName(true) + ".go");
  }
  
  @NotNull
  @Override
  protected String getBasePath() {
    return "inspections/go-defer-function-call";
  }
}
