package com.goide.inspections;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.unresolved.*;
import com.goide.project.GoModuleLibrariesService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.ThrowableComputable;
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
      GoDuplicateMethodInspection.class,
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
  
  public void testImportIgnoringDirectories() throws IOException {
    final VirtualFile root = myFixture.getTempDirFixture().findOrCreateDir("root");
    myFixture.getTempDirFixture().findOrCreateDir("root/src/to_import/testdata");
    myFixture.getTempDirFixture().findOrCreateDir("root/src/to_import/.name");
    myFixture.getTempDirFixture().findOrCreateDir("root/src/to_import/_name");
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(root.getUrl());
    doTest();
  }
  
  public void testRelativeImportIgnoringDirectories() throws IOException {
    myFixture.getTempDirFixture().findOrCreateDir("to_import/testdata");
    myFixture.getTempDirFixture().findOrCreateDir("to_import/.name");
    myFixture.getTempDirFixture().findOrCreateDir("to_import/_name");
    doTest();
  }
  
  public void testDoNotReportNonLastMultiResolvedImport() throws IOException {
    final VirtualFile root1 = myFixture.getTempDirFixture().findOrCreateDir("root1");
    final VirtualFile root2 = myFixture.getTempDirFixture().findOrCreateDir("root2");
    
    myFixture.getTempDirFixture().findOrCreateDir("root1/src/to_import/unique");
    myFixture.getTempDirFixture().findOrCreateDir("root1/src/to_import/shared");
    myFixture.getTempDirFixture().findOrCreateDir("root2/src/to_import/shared");
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(root1.getUrl(), root2.getUrl());
    doTest();
  }
  
  public void testLocalScope() {
    myFixture.configureByText("a.go", "package foo; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func init(){bar()}");
    myFixture.checkHighlighting();
  }
  
  public void testInnerTypesFromTestDataPackage() throws Throwable {
    final VirtualFile root = myFixture.getTempDirFixture().findOrCreateDir("root");
    ApplicationManager.getApplication().runWriteAction(new ThrowableComputable<Object, Throwable>() {
      @Override
      public Object compute() throws Throwable {
        VirtualFile file = myFixture.getTempDirFixture().findOrCreateDir("root/src/to_import/testdata").createChildData(this, "test.go");
        myFixture.saveText(file, "package testdata;\n" +
                                 "type Reader interface {\n" +
                                 "\tRead(p []byte) (n int, err error)\n" +
                                 "}");
        return null;
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(root.getUrl());
    doTest();
  }
  
  public void testInnerTypesFromOtherPackage() {
    myFixture.configureByText("main.go", "package main; import \"io\"; type Outer struct { *io.LimitedReader };" +
                                         "func main() { _ = &Outer{LimitedReader: &io.LimitedReader{}, } }");
    myFixture.checkHighlighting();
  }
  
  public void testDuplicatesNoLocalResolveForTest() {
    myFixture.configureByText("a.go", "package i; type P struct { v1 int }");
    myFixture.configureByText("b.go", "package i_test; import ( \".\" ); func <warning>f</warning>() { print(i.P{}.<error>v1</error>) }");
    myFixture.checkHighlighting();
  }
  
  public void testDuplicatesInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }

  public void testDuplicateMethodsInOnePackage() {
    myFixture.configureByText("a.go", "package main; type Foo int; func (f Foo) bar(a, b string) {}");
    myFixture.configureByText("b.go", "package main; func (a *Foo) <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }

  public void testNoDuplicateMethodsInOnePackage() {
    myFixture.configureByText("a.go", "package main; type Foo int; func (f Foo) bar() {}");
    myFixture.configureByText("b.go", "package main; type Baz int; func (f Baz) bar() {}");
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
