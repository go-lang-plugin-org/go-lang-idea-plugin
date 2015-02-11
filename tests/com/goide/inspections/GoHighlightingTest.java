package com.goide.inspections;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.unresolved.GoAssignmentToConstantInspection;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.goide.inspections.unresolved.GoUnusedFunctionInspection;
import com.goide.inspections.unresolved.GoUnusedVariableInspection;
import com.goide.project.GoModuleLibrariesService;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    myFixture.enableInspections(
      GoUnresolvedReferenceInspection.class,
      GoDuplicateFieldsOrMethodsInspection.class,
      GoUnusedVariableInspection.class,
      GoUnusedFunctionInspection.class,
      GoAssignmentToConstantInspection.class,
      GoDuplicateFunctionInspection.class,
      GoDuplicateArgumentInspection.class,
      GoDuplicateReturnArgumentInspection.class,
      GoFunctionVariadicParameterInspection.class,
      GoVarDeclarationInspection.class,
      GoNoNewVariablesInspection.class,
      GoReturnInspection.class,
      GoFunctionCallInspection.class
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

  public void testSimple()      { doTest(); }
  public void testStruct()      { doTest(); }
  public void testBoxes()       { doTest(); }
  public void testRanges()      { doTest(); }
  public void testVars()        { doTest(); }
  public void testRecv()        { doTest(); }
  public void testPointers()    { doTest(); }
  public void testSlices()      { doTest(); }
  public void testShortVars()   { doTest(); }
  public void testReturns()     { doTest(); }
  public void testRequest()     { doTest(); }
  public void testStop()        { doTest(); }
  public void testVarBlocks()   { doTest(); }
  public void testBlankImport() { doTest(); }
  public void testVariadic()    { doTest(); }
  public void testCheck()       { doTest(); }
  public void testCheck_test()  { doTest(); }
  public void testFuncCall()    { doTest(); }
  public void testBackticks()   { doTest(); }
  public void testConsts()      { doTest(); }
  public void testFields()      { doTest(); }
  
  public void testDoNotReportNonLastMultiResolvedImport() throws IOException {
    final VirtualFile root1 = myFixture.getTempDirFixture().findOrCreateDir("root1");
    final VirtualFile root2 = myFixture.getTempDirFixture().findOrCreateDir("root2");
    final List<String> rootUrls = Arrays.asList(root1.getUrl(), root2.getUrl());
    
    myFixture.getTempDirFixture().findOrCreateDir("root1/src/to_import/unique");
    myFixture.getTempDirFixture().findOrCreateDir("root1/src/to_import/shared");
    myFixture.getTempDirFixture().findOrCreateDir("root2/src/to_import/shared");
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(rootUrls);
    doTest();
  }
  
  public void testLocalScope() {
    myFixture.configureByText("a.go", "package foo; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func init(){bar()}");
    myFixture.checkHighlighting();
  }
  
  public void testDuplicatesInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }
  
  public void testInitInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func init() {bar()}; func <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }
  
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }
}
