package com.goide.inspections;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.unresolved.*;
import com.goide.project.GoModuleLibrariesService;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;

import java.io.IOException;

public class GoHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    myFixture.enableInspections(
      GoUnresolvedReferenceInspection.class,
      GoDuplicateFieldsOrMethodsInspection.class,
      GoUnusedVariableInspection.class,
      GoUnusedGlobalVariableInspection.class,
      GoUnusedFunctionInspection.class,
      GoAssignmentToConstantInspection.class,
      GoDuplicateFunctionInspection.class,
      GoDuplicateArgumentInspection.class,
      GoDuplicateReturnArgumentInspection.class,
      GoFunctionVariadicParameterInspection.class,
      GoVarDeclarationInspection.class,
      GoNoNewVariablesInspection.class,
      GoReturnInspection.class,
      GoFunctionCallInspection.class,
      GoDeferGoInspection.class
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
  public void testSelector()    { doTest(); }
  public void testComposite()   { doTest(); }
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
  public void testFuncLiteral() { doTest(); }
  public void testTypeLiterals(){ doTest(); }
  public void testFuncType()    { doTest(); }
  public void testTemplates()   { doTest(); }
  public void testInterfaces()  { doTest(); }
  public void testReceiverType(){ doTest(); }
  public void testForRange()    { doTest(); }
  public void testMismatch()    { doTest(); }
  public void testStubParams()  { doTest(); }
  
  public void testDoNotReportNonLastMultiResolvedImport() throws IOException {
    final VirtualFile root1 = myFixture.getTempDirFixture().findOrCreateDir("root1");
    final VirtualFile root2 = myFixture.getTempDirFixture().findOrCreateDir("root2");
    
    myFixture.getTempDirFixture().findOrCreateDir("root1/src/to_import/unique");
    myFixture.getTempDirFixture().findOrCreateDir("root1/src/to_import/shared");
    myFixture.getTempDirFixture().findOrCreateDir("root2/src/to_import/shared");
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(root1.getUrl(), root2.getUrl());
    try {
      doTest();
    }
    finally {
      GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls();
    }
  }
  
  public void testLocalScope() {
    myFixture.configureByText("a.go", "package foo; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func init(){bar()}");
    myFixture.checkHighlighting();
  }
  
  public void testInnerTypesFromOtherPackage() {
    myFixture.configureByText("main.go", "package main; import \"io\"; type Outer struct { *io.LimitedReader };" +
                                         "func main() { _ = &Outer{LimitedReader: &io.LimitedReader{}, } }");
    myFixture.checkHighlighting();
  }
  
  public void testDuplicatesInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }
  
  public void testInitInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func init() {bar()}; func <error>bar</error>() {}; func init() {}");
    myFixture.checkHighlighting();
  }

  public void testMainInFooPackage() {
    myFixture.configureByText("a.go", "package foo; func main() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func <error>main</error>() {bar()}; func <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }
  
  public void testMainInMainPackage() {
    myFixture.configureByText("a.go", "package main; func main() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package main; func main() {bar()}; func <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }
  
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }
}
