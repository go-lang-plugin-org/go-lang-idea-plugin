package com.goide.inspections;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.unresolved.GoAssignmentToConstantInspection;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.goide.inspections.unresolved.GoUnusedFunctionInspection;
import com.goide.inspections.unresolved.GoUnusedVariableInspection;
import com.intellij.testFramework.LightProjectDescriptor;

public class GoHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    myFixture.enableInspections(
      new GoUnresolvedReferenceInspection(),
      new GoDuplicateFieldsOrMethodsInspection(),
      new GoUnusedVariableInspection(),
      new GoUnusedFunctionInspection(),
      new GoAssignmentToConstantInspection(),
      new GoDuplicateFunctionInspection()
    );
  }

  private void doTest() {
    myFixture.testHighlighting(true, false, false, getTestName(true) + ".go");
  }

  @Override
  protected String getBasePath() {
    return "highlighting";
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  public void testSimple()    { doTest(); }
  public void testStruct()    { doTest(); }
  public void testBoxes()     { doTest(); }
  public void testRanges()    { doTest(); }
  public void testVars()      { doTest(); }
  public void testPointers()  { doTest(); }
  
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }
}
